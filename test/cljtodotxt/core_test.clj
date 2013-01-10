(ns cljtodotxt.core-test
  (:use clojure.test
        cljtodotxt.core))

(deftest test-priority
  (is (= (:priority (parse-line "(A) Call Mom")) "A"))
  (is (= (:priority (parse-line "Really gotta call Mom (A) @phone @someday")) nil))
  (is (= (:priority (parse-line "(b) Get back to the boss")) nil))
  (is (= (:priority (parse-line "(B)->Submit TPS report")) nil)))

(deftest test-creation
  (is (= (:created (parse-line "2011-03-02 Document +TodoTxt task format"))  #inst "2011-03-02T00:00:00.000-00:00"))
  (is (= (:created (parse-line "(A) 2011-03-02 Call Mom"))  #inst "2011-03-02T00:00:00.000-00:00"))
  (is (= (:created (parse-line "(A) Call Mom 2011-03-02")) nil)))

(deftest test-contexts-and-projects
  (let [task (parse-line "(A) Call Mom +Family +PeaceLoveAndHappiness @iphone @phone")]
    (is (= (:contexts task) ["@iphone" "@phone"]))
    (is (= (:projects task) ["+Family" "+PeaceLoveAndHappiness"]))))

(deftest test-completed
  (is (:complete (parse-line "x 2011-03-03 Call Mom")))
  (is (not (:complete (parse-line "xylophone lesson"))))
  (is (not (:complete (parse-line "X 2012-01-01 Make resolutions"))))
  (is (= (:completed (parse-line "x 2011-03-02 2011-03-01 Review Tim's pull request +TodoTxtTouch @github"))
         #inst "2011-03-02T00:00:00.000-00:00")))
