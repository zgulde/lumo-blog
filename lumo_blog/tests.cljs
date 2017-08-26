(ns lumo-blog.tests
  (:require [lumo-blog.validation :as validate]
            [cljs.test :as test]))

(def good-post {:title "blog post" :body "lorem ipsum dolor sit amet"})
(def bad-post {:title ""})

(test/deftest test-post-validation

  (test/testing "returns a map with :fails, :passes and :errors properties"
    (let [validation (validate/post {:title "foo" :body "bar"})]
      (test/is (boolean? (:fails validation)))
      (test/is (boolean? (:passes validation)))
      (test/is (map? (:errors validation)))))

  (test/testing "fails if the title or body property is missing"
    (let [post {:title "title"}]
      (test/is (:fails (validate/post post)))
      (test/is (not (:passes (validate/post post)))))
    (let [post {:body "body"}]
      (test/is (:fails (validate/post post)))
      (test/is (not (:passes (validate/post post))))))

  (test/testing "provides error messages for each empty/missing property"
    (let [validation (validate/post {})
          title-errors (:title (:errors validation))
          body-errors (:body (:errors validation))]
      (test/is (seq title-errors) "title errors are present")
      (test/is (string? (first title-errors)))
      (test/is (seq body-errors) "body errors are present")
      (test/is (string? (first body-errors))))))

(test-post-validation)
