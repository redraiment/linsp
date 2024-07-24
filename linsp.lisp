(define null?
  (lambda (element) (eq element nil)))

(define caar
  (lambda (list) (car (car list))))

(define cadr
  (lambda (list) (car (cdr list))))

(define caddr
  (lambda (list) (car (cdr (cdr list)))))

(define cdar
  (lambda (list) (cdr (car list))))

(define cadar
  (lambda (list) (car (cdr (car list)))))

(define not
  (lambda (condition) (cond (condition nil) (t t))))

(define and
  (lambda (x y)
    (cond (x (cond (y t)
                   (t nil)))
          (t nil))))

(define subst
  (lambda (pattern replace element)
    (cond ((atom element) (cond ((eq element pattern) replace)
                                (t element)))
          (t (cons (subst pattern replace (car element))
                   (subst pattern replace (cdr element)))))))

(define append
  (lambda (x y)
    (cond ((null? x) y)
          (t (cons (car x)
                   (append (cdr x) y))))))

(define pair
  (lambda (x y)
    (cond ((and (null? x) (null? y)) nil)
          (t (cons (cons (car x) (cons (car y) nil))
                   (pair (cdr x) (cdr y)))))))

(define assoc
  (lambda (key elements)
    (cond ((eq key (caar elements)) (cadar elements))
          ((not (null? elements)) (assoc key (cdr elements))))))

(define eval
  (lambda (code bindings)
    (cond
     ((atom code) (assoc code bindings))
     ((atom (car code)) (cond
                         ((eq (car code) 'fn) code)
                         ((eq (car code) 'quote) (cadr code))

                         ((eq (car code) '=)
                          (eq (eval (cadr code) bindings)
                              (eval (caddr code) bindings)))
                         ((eq (car code) 'first)
                          (car (eval (cadr code) bindings)))
                         ((eq (car code) 'second)
                          (cadr (eval (cadr code) bindings)))
                         ((eq (cdr code) 'next)
                          (cdr (eval (cadr code) bindings)))
                         ((eq (cdr code) 'cons)
                          (cons (eval (cadr code) bindings)
                                (eval (caddr code) bindings)))

                         ((eq (car code) 'if)
                          (cond ((eval (cadr code) bindings)
                                 (eval (caddr code) bindings))
                                (t (eval (caddr (cdr code)) bindings))))))
     ((eq (caar code) 'fn)
      (eval (caddr code)
            (append (pair (cadar code)
                          (eval-list (cdr code) bindings))
                    bindings))))))

(define eval-list
  (lambda (codes bindings)
    (cond ((null? codes) nil)
          (t (cons (eval (car codes) bindings)
                   (eval-list (cdr codes) bindings))))))

(eval '(= '2 (second '(1 2 3))) '((one 1) (two 2)))
