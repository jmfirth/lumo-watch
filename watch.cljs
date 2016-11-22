(ns watch
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as string]))

(def watchr (nodejs/require "watchr"))
(def net (nodejs/require "net"))
(def fs (nodejs/require "fs"))

(defn createClient [port host]
  (let [client (net.Socket)]
    (do
      (.on client "data" #(print (.toString %)))
      (.on client "close" #(print "socket connected"))
      (.connect client port host #(print "socket destroyed")))))

(def client (createClient 5555 "localhost"))

(def path (process.cwd))

(defn read-file [fullPath]
  (.toString (fs.readFileSync fullPath)))

(defn listener [changeType fullPath currentStat previousStat]
  (case changeType
    "update" (.write client (string/join [(read-file fullPath) "\n"]))
    "create" (.write client (string/join [(read-file fullPath) "\n"]))
    "delete" (print "the file" fullPath "was deleted" previousStat)))

(defn next [err]
  (if (nil? err)
    (print "watch successful on" path)
    (print "watch failed on" path "with error" err)))

(def stalker (.open watchr path listener next))

