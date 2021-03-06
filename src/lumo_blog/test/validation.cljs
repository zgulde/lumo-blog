(ns lumo-blog.test.validation
  (:require [lumo-blog.validation :as validate]
            [cljs.test :as test]
            [lumo-blog.util :as util]))

(def good-post {:title "blog post" :body "lorem ipsum dolor sit amet"})
(def bad-post {:title ""})

(test/deftest test-post-validation

  (test/testing "returns a map with :fails, :passes and :errors properties"
    (let [validation (validate/post {:title "foo" :body "bar"})]
      (test/is (boolean? (:fails validation)))
      (test/is (boolean? (:passes validation)))
      (test/is (map? (:errors validation)))))

  (test/testing "fails if the title is missing"
    (let [post {:title "title"}]
      (test/is (:fails (validate/post post)))
      (test/is (not (:passes (validate/post post))))))

  (test/testing "fails if the body is missing"
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

(test/deftest test-user-validation

  (test/testing "fails without an email address"
    (let [validation (validate/user {:password "abcd"})]
      (test/is (:fails validation))
      (test/is (string? (first (:email (:errors validation)))))))

  (test/testing "fails with an invalid email address"
    (let [validation (validate/user {:email "abcd" :password "abcd"})]
      (test/is (:fails validation))
      (test/is (string? (first (:email (:errors validation)))))))

  (test/testing "fails without a password"
    (let [validation (validate/user {:email "test@gmail.com"})]
      (test/is (:fails validation))
      (test/is (string? (first (:password (:errors validation)))))))

  (test/testing "fails with an empty password"
    (let [validation (validate/user {:email "test@gmail.com" :password ""})]
      (test/is (:fails validation))
      (test/is (string? (first (:password (:errors validation)))))))

  (test/testing "a valid user passes validation"
    (let [user {:email "test@test.com" :password "abcde"}
          validation (validate/user user)]
      (test/is (= true (:passes validation)))
      (test/is (= false (:fails validation)))
      (test/is (empty? (:errors validation))))))

(defn run []
  (util/log-info "Running validation tests...")
  (test-post-validation)
  (test-user-validation)
  (util/log-info "Finished validation tests!"))

(defn -main []
  (run))
