(ns lumo-blog.test.core
  (:require [lumo-blog.util :as util]
            [lumo-blog.test.api :as api-tests]
            [lumo-blog.test.validation :as validation-tests]))

(defn -main [& args]
  (util/log-info "Running all tests...")
  (validation-tests/run)
  (api-tests/run))
