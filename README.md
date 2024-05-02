# Linsp

A Tiny Lisp Interpreter in (Emacs) Lisp.

用Emacs Lisp实现一个超轻量级的`Lisp`解释器。

并不是为了发明一门新的编程语言，而是看了Paul Graham的《[the root of lisp](http://www.paulgraham.com/rootsoflisp.html)》产生了灵感：先快速实现一个精简版的Lisp语言解释器，再用这个精简的Lisp语言重新实现一个具有高级特性的Lisp语言解释器。

这个思路很像PyPy，就像“上帝说要有光，于是就有了光”一样，上帝创造了最基本的东西，它们通过组合又造就了欣欣向荣的大千世界。实现`Linsp`会让你有上帝的感觉。

# 解释器（linsp.el）

整个解释器只有一个文件`linsp.el`，采用`Emacs Lisp`实现。为证明`Linsp`是一门与`Emacs Lisp`不同的`Lisp`，`Linsp`使用`Lisp-1`，并且支持闭包。

# 示例（factorial.linsp）

用`Linsp`实现的递归求阶乘。

# 用法（Usage）

需要先安装好[Emacs](https://www.gnu.org/software/emacs/)编辑器，然后执行：

```sh
emacs -Q --script linsp.el factorial.linsp
```
