(ns foo.core-test
  (:require
   [clojure.test :as t]
   [foo.core :as sut]))

(t/deftest myinc-test
  (t/is (= 2 (sut/myinc 1)))
  (t/is (= 3 (sut/myinc 2))))
