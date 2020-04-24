#!/usr/bin/env bb

(ns entrypoint
  (:require
    [clojure.java.io :as io]
    [clojure.java.shell :as shell]
    [clojure.string :as str]))

(defn- file->ns-name
  [root-file file]
  (let [relpath (subs (.getAbsolutePath file)
                      (inc (count (.getAbsolutePath root-file))))
        sep (System/getProperty "file.separator")]
    (-> relpath
        (str/replace #"\.clj$" "")
        (str/replace #"_" "-")
        (.replace sep "."))))

(defn- list-files
  [file]
  (->> (seq (.listFiles file))
       (mapcat #(if (.isFile %) [%] (list-files %)))))

(defn- list-test-namespaces
  [root-path test-file-re]
  (let [root (io/file root-path)]
    (->> (list-files root)
         (filter #(re-seq test-file-re (.getName %)))
         (map #(file->ns-name root %)))))

(defn- test-code
  [namespaces]
  `(let [ns-syms# (map symbol ~namespaces)]
     (doseq [x# ns-syms#]
       (require (symbol x#)))

     (let [res# (apply clojure.test/run-tests ns-syms#)]
       (System/exit (+ (:fail res#) (:error res#))))))

(defn -main
  [[source-paths test-paths test-file-pattern]]
  (let [source-paths (str/split source-paths #" ")
        test-paths (str/split test-paths #" ")
        test-file-re (re-pattern test-file-pattern)

        classpaths (str/join ":" (concat source-paths test-paths))
        namespaces (vec (mapcat #(list-test-namespaces % test-file-re) test-paths))
        {:keys [exit] :as res} (apply shell/sh ["bb" "--classpath" classpaths
                                            (str (test-code namespaces))])]
    (some-> res :out println)
    (some-> res :err println)
    (System/exit exit)))

(if (not= 3 (count *command-line-args*))
  (do (println "Usage: ./entrypoint.clj 'src-paths' 'test-patsh' 'test-file-pattern'")
      (System/exit 1))
  (-main *command-line-args*))
