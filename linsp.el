#!/bin/emacs --script

(require 'cl)

;;; Interpreter
;; Scope
(defun binding-lookup (bindings name)
  (some (lambda (table)
          (gethash name table))
        bindings))

(defun binding-set (bindings name value)
  (puthash name value (car bindings)))

(defun binding-bind (args params)
  (let ((local (list (make-hash-table))))
    (binding-set local 'args params)
    (loop for name in args
          for value in params
          do (binding-set local name value))
    (car local)))

;; Utilities
(defalias 'partial #'apply-partially)

(defun cpply (bindings fn args)
  (apply fn (mapcar (partial #'clop bindings) args)))

(defun arity (fn &rest args)
  (do ((a args (cdr a))
       (r t (funcall fn (first a) (second a))))
      ((not (cdr a)) r)))

;; eval
(defun eval-atom (bindings object)
  (if (symbolp object)
    (binding-lookup bindings object)
    object))

(defun eval-call (bindings object)
  (destructuring-bind (e &rest args) object
    (let ((v (binding-lookup bindings e)))
      (cond
       ((memq e '(quote eval def if do))
        (funcall (binding-lookup bindings e) bindings args)) ; special form
       ((eq e 'fn) (append (list :lambda bindings) args))
       ((eq e 'm4) (append (list :macro bindings) args))
       ((cadr? e) (clop-cadr bindings object))
       ((and v (functionp v)) (cpply bindings v args))
       (v (clop bindings (cons v (cdr object))))
       ((functionp e) (cpply bindings e args))
       (t (error "Symbol's function definition is void: %s" e))))))

(defun eval-lambda (object)
  (destructuring-bind ((type closure args &rest body) &rest params) object
    (clop (cons (binding-bind args params) closure)
          (cons 'do body))))

(defun eval-nest (bindings object)
  (case (caar object)
    (:lambda (eval-lambda (cons (car object)
                                (mapcar (partial #'clop bindings) (cdr object)))))
    (:macro (clop bindings (eval-lambda object)))
    (t (clop bindings (cons (clop bindings (car object))
                            (cdr object))))))

(defun eval-list (bindings object)
  (if (atom (car object))
    (eval-call bindings object)
    (eval-nest bindings object)))

(defun clop (bindings object)
  (if (atom object)
    ;; atom
    (eval-atom bindings object)
    ;; function call
    (eval-list bindings object)))

;;; build-in function
(defun clop-quote (bindings object)
  (car object))

(defun clop-eval (bindings object)
  (clop bindings (clop bindings (car object))))

(defun clop-def (bindings object)
  (do* ((o object (nthcdr 2 o))
        (v (clop bindings (second o))
           (if o (clop bindings (second o)) v)))
      ((null o) v)
    (binding-set bindings (first o) v)))

(defun clop-if (bindings object)
  (do* ((o object (nthcdr 2 o))
        (e (second o) (if (cdr o) (second o) (first o))))
      ((or (null (cdr o))
           (clop bindings (car o)))
       (clop bindings e))))

(defun clop-progn (bindings object)
  (do ((e object (cdr e))
       (r nil (clop bindings (car e))))
      ((null e) r)))

(defun cadr? (object)
  (and (symbolp object)
       (string-match-p "^c[ad]+r$" (symbol-name object))))

(defun clop-cadr (bindings object)
  (do ((v (clop bindings (second object))
          (if (= (car o) ?a) (car v) (cdr v)))
       (o (cdr (reverse (string-to-list (symbol-name (car object)))))
          (cdr o)))
      ((= (car o) ?c) v)))

;;; Default Package
(defvar global-scope (list (make-hash-table)))

;; special form
(binding-set global-scope 'quote #'clop-quote)
(binding-set global-scope 'eval #'clop-eval)
(binding-set global-scope 'def #'clop-def)
(binding-set global-scope 'if #'clop-if)
(binding-set global-scope 'do #'clop-progn)

;; predicate?
(binding-set global-scope 'int? #'integerp)
(binding-set global-scope 'number? #'numberp)
(binding-set global-scope 'empty? #'null)
(binding-set global-scope 'atom? #'atom)
(dolist (op '(= /= < <= > >=))
  (binding-set global-scope op (partial #'arity op)))

(defun read-from-file (file-name)
  (with-temp-buffer
    (insert-file-contents file-name)
    (car (read-from-string (concat "(do " (buffer-string) ")")))))

(dolist (script-file (nthcdr 3 command-line-args))
  (dolist (e (read-from-file script-file))
    (clop global-scope e)))
