# Linsp

用Kotlin/Native先实现一个超轻量级的`Lisp`解释器，这个解释器只提供了在Paul Graham的《[The Root of Lisp](http://www.paulgraham.com/rootsoflisp.html)》文章中提到的7个原子操作，即`quote`、`atom`、`eq`、`car`、`cdr`、`cons`、`cond`，外加`lambda`支持。然后在`main.linsp`中用这七个原子操作与`lambda`实现一门新的Lisp语言，提供不一样的操作符，如`first`、`second`、`next`、`=`、`if`等。

这种方法与PyPy很像。等后续有时间我再详细介绍如何实现Linsp。

# Quick Start

```sh
git clone https://github.com/redraiment/linsp.git
cd linsp
./gradlew linkNative
build/bin/native/releaseExecutable/linsp.kexe main.linsp
```
