(ns cljtodotxt.core)

(defn -parse-date
  [s]
  (if (not (clojure.string/blank? s))
    ;; horrible hack on timezones - fix this
    (.. (java.text.SimpleDateFormat. "yyyy-MM-ddZ") (parse (str s "+0000")))))

(defn -parse-contexts
  [line]
  (re-seq #"@[^ ]+" line))

(defn -parse-projects
  [line]
  (re-seq #"\+[^ ]+" line))

(defn parse-line
  [line]
  (let [[_ completed priority created task]
        (re-find #"^(x [0-9]{4}-[0-9]{2}-[0-9]{2} |)(\([A-Z]\) |)([0-9]{4}-[0-9]{2}-[0-9]{2} |)(.*)" line)
        contexts (re-seq #"@[^ ]+" line)
        projects (re-seq #"\+[^ ]+" line)
        complete? (not (clojure.string/blank? completed))]
    (hash-map :complete complete?
              :completed (if complete? (-parse-date (subs completed 2)))
              :created (-parse-date created)
              :contexts contexts
              :priority (if (not (clojure.string/blank? priority)) (subs priority 1 2))
              :projects projects
              :task task
              :full line)))

(defn parse-file
  [filename]
  (map parse-line (clojure.string/split-lines (slurp filename))))


;; aggregators on tasks

(defn distinct-things
  [key]
  (fn [tasks]
    (distinct (remove nil? (flatten (map key tasks))))))

(def all-projects (distinct-things :projects))
(def all-contexts (distinct-things :contexts))
(def all-priorities (distinct-things :priority))

(defn search-tasks
  [str tasks]
  (filter #(re-find (java.util.regex.Pattern/compile str) (:original %)) tasks))
