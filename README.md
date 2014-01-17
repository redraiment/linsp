# Linsp

A Tiny Lisp in Lisp (`Emacs Lisp`, `Common Lisp` etc.)

用Lisp (`Emacs Lisp`、`Common Lisp`等)实现一个超轻量级的`Lisp`解释器。

并不是为了发明一门新的编程语言，而是看了Paul Graham的《[the root of lisp](http://www.paulgraham.com/rootsoflisp.html)》产生了灵感：先快速实现一个最精简的Lisp语言，再基于这个精简的语言实现其他高级的功能。

这就像“上帝说要有光，于是就有了光”一样，上帝创造了最基本的东西，它们通过组合又造就了欣欣向荣的大千世界。实现`Linsp`会让你有上帝的感觉。

## linsp.el

用`Emacs Lisp`实现的`Lisp`解释器

为证明`Linsp`是一门与`Emacs Lisp`不同的`Lisp`，`Linsp`使用`Lisp-1`，并且支持闭包。

## linsp.lisp

`Common Lisp`版的`Lisp`解释器

功能与`Emacs Lisp`版完全一样，但拥有比后者更好的性能！

## factorial.linsp

用`Linsp`实现的递归求阶乘
