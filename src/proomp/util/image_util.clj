(ns proomp.util.image-util
  (:require [cambium.core :as log]
            [proomp.util.file-util :as file-util]
            [libpython-clj2.python :refer [py.] :as py]))


(defn save-python-image [image file-name]
  (if (file-util/file-exists? file-name)
    (log/warn {"File exists. Skipping." file-name})
    (do
      (log/debug {"Saving file." file-name})
      (py. image "save" file-name))))
