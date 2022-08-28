(defn evaluate [exp varValue] ((.evalFunc exp) varValue))
(defn toString [exp] (.toStr exp))
(defn toStringSuffix [exp] (.toSuff exp))
(defn diff [exp varName] ((.diff exp) varName))

(definterface PartOfExpression
  (evalFunc[])
  (toStr[])
  (toSuff[])
  (diff[])
  )

(deftype Const [value]
  PartOfExpression
  (evalFunc [this] (fn [varValue] (double value)))
  (toStr [this] (format "%.1f" (double value)))
  (toSuff [this] (format "%.1f" (double value)))
  (diff [this] (fn [varName] (Const. 0)))
  )
(defn Constant [value] (Const. value))

(deftype Var [varName]
  PartOfExpression
  (evalFunc [this] (fn [varValue] (varValue (str (get (clojure.string/lower-case varName) 0)))))
  (toStr [this] (str varName))
  (toSuff [this] (str varName))
  (diff [this] (fn [varName'] (if (= varName varName') (Constant 1) (Constant 0))))
  )
(defn Variable [varName] (Var. varName))

(deftype Operation [op opName args diffFunc]
  PartOfExpression
  (evalFunc [this] (fn [varValue] (apply op (map (fn [arg] (evaluate arg varValue)) args))))
  (toStr [this] (str "(" opName " " (clojure.string/join " " (map toString args)) ")" ))
  (toSuff [this] (str "("(clojure.string/join " " (map toStringSuffix args)) " " opName ")" ))
  (diff [this] (fn [varName] (apply diffFunc args (map (fn [arg] (diff arg varName)) args))))
  )

(defn Add [& args] (Operation. + "+" args (fn [args da db] (Add da db))))
(defn Subtract [& args] (Operation. - "-" args (fn [args da db] (Subtract da db))))
(defn Multiply [& args] (Operation. * "*" args (fn [args da db]
                                                 (Add
                                                  (Multiply da (nth args 1))
                                                  (Multiply (nth args 0) db)))))
(defn Divide [& args] (Operation. (fn [a b] (/ (double a) (double b))) "/" args (fn [args da db] (Divide
                                                                                                  (Subtract
                                                                                                   (Multiply da (nth args 1))
                                                                                                   (Multiply (nth args 0) db))
                                                                                                  (Multiply (nth args 1) (nth args 1))))))
(defn toBool [a] (if (> a 0) true false))
(defn toInt [a] (if a 1 0))
(defn And [& args] (Operation. (fn [a b] (toInt (and (toBool a) (toBool b)))) "&&" args (fn [] ())))
(defn Or [& args] (Operation. (fn [a b] (toInt (or (toBool a) (toBool b)))) "||" args (fn [] ())))
(defn Xor [& args] (Operation. (fn [a b] (toInt (not (= (toBool a) (toBool b))))) "^^" args (fn [] ())))
(defn Negate [& args] (Operation. - "negate" args  (fn [args da] (Negate da))))
(declare Sinh)
(declare Cosh)
(defn Sinh [& args] (Operation. (fn [arg] (Math/sinh arg)) "sinh" args
                                (fn [args da] (Multiply da (Cosh (nth args 0))))))
(defn Cosh [& args] (Operation. (fn [arg] (Math/cosh arg)) "cosh" args
                                (fn [args da] (Multiply da (Sinh (nth args 0))))))
(def operations {'+      Add,
                 '-      Subtract,
                 '*      Multiply,
                 '/      Divide,
                 'negate Negate
                 'sinh   Sinh,
                 'cosh   Cosh,
                 '&& And,
                 '|| Or,
                 (symbol "^^") Xor
                })

(defn parse [expression]
  (cond
   (number? expression) (Constant expression)
   (symbol? expression) (Variable (str expression))
   (list? expression) (apply (operations (first expression)) (map parse (rest expression)))
   ))
(defn parseObject  [line] (parse (read-string line)))


(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _empty [value] (partial -return value))
(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c))
      (-return c cs))))
(defn _map [f result]
  (if (-valid? result)
    (-return (f (-value result)) (-tail result))))
(defn _combine [f a b]
  (fn [input]
    (let [ar ((force a) input)]
      (if (-valid? ar)
        (_map (partial f (-value ar))
              ((force b) (-tail ar)))))))
(defn _either [a b]
  (fn [input]
    (let [ar ((force a) input)]
      (if (-valid? ar)
        ar
        ((force b) input)))))
(defn _parser [p]
  (let [pp (_combine (fn [v _] v) p (_char #{\u0000}))]
    (fn [input] (-value (pp (str input \u0000))))))

(defn +char [chars]
  (_char (set chars)))
(defn +char-not [chars]
  (_char (comp not (set chars))))
(defn +map [f parser]
  (comp (partial _map f) parser))
(def +parser _parser)
(def +ignore (partial +map (constantly 'ignore)))
(defn iconj [coll value]
  (if (= value 'ignore)
    coll
    (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps]
  (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps]
  (apply +seqf #(nth %& n) ps))
(defn +or [p & ps]
  (reduce _either p ps))
(defn +opt [p]
  (+or p (_empty nil)))
(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))]
         (rec)))
(defn +plus [p]
  (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))

(def *digit (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digit))))
(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(def *null (+seqf (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l")))
(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))

(def *const (+map (comp Constant read-string) (+str (+seq (+opt(+char "-")) *number (+opt (+seq (+char ".") *digit))))))
(def *variable_char (+char "xyzXYZ"))
(def *variable (+map Variable (+str (+plus *variable_char))))
(def *operation-char (+char "+-*/&|^negate"))
(def *operation-symbol (+map symbol (+str (+plus *operation-char))))
(def *tokens (+or *const *variable))
(declare *expression)

(def *list (+seqn 1 (+char "(") (+plus (+seqn 0 *ws (delay *expression))) *ws (+char ")")))
(def *operation (+map #(apply (operations (last %)) (butlast %)) *list))

(def *expression (+or *operation *tokens *operation-symbol))

(def parseObjectSuffix (+parser (+seqn 0 *ws *expression *ws)))