package me.zzp.interpreter

import me.zzp.interpreter.Expression.Atom.Companion.nil

/**
 * 表达式，即S-Exp。
 */
sealed interface Expression {

    /**
     * 原子表达式：当前版本只支持符号（Symbol）原子。
     */
    data class Atom(val name: String) : Expression {

        /**
         * 字面量。
         */
        companion object {
            /**
             * 逻辑真
             */
            val t = Atom("t")

            /**
             * 逻辑假：空列表。
             */
            val nil = Atom("nil")

            /**
             * 引用。
             */
            val quote = Atom("quote")
        }

        /**
         * 符号表达式是原子。
         */
        override fun isAtom(): Expression = t

        /**
         * 符号比较仅比较名称是否一致。
         */
        override fun eq(that: Expression): Expression =
            if (that is Atom && this.name == that.name) t else nil

        /**
         * 从上下文中找出对应的表达式。
         */
        override fun eval(context: Bindings): Expression =
            context[name]

        /**
         * 原子展示为名字。
         */
        override fun toString() = name
    }

    /**
     * 非空链表单元。
     * [car]是链表头部元素。
     * [cdr]是链表余部元素：可为空、单个元素、另一个单元。
     *
     * 注意：当[car]为[nil]时，值为`(nil)`，而不是`nil`。
     */
    data class Cell(val car: Expression, val cdr: Expression = nil) : Expression {

        /**
         * 非空链表不是原子。
         */
        override fun isAtom(): Expression = nil

        /**
         * 非空链表不等于任何其他表达式。
         */
        override fun eq(that: Expression): Expression = nil

        /**
         * 链表作为函数调用执行：
         * - [car]为操作符。
         * - [cdr]为操作数。
         */
        override fun eval(context: Bindings): Expression =
            if (car is Atom) {
                when (val fn = car.name) {
                    "lambda" -> this
                    "quote" -> quote(cdr)
                    "atom" -> atom(context, cdr)
                    "eq" -> same(context, cdr)
                    "car" -> car(context, cdr)
                    "cdr" -> cdr(context, cdr)
                    "cons" -> cons(context, cdr)
                    "cond" -> cond(context, cdr)
                    else -> Cell(context[fn].eval(context), cdr).eval(context) // 执行函数
                }
            } else if (car is Cell && car.car is Atom && car.car.name == "lambda") {
                var (arguments, body) = binary(car.cdr)
                var parameters = cdr
                val bindings = Bindings(context)
                while (arguments is Cell && parameters is Cell) { // 形参与实参绑定
                    val argument = arguments.car
                    val parameter = parameters.car

                    if (argument is Atom) {
                        bindings[argument.name] = parameter.eval(context)
                    } else {
                        error("lambda argument $argument is not a symbol")
                    }

                    arguments = arguments.cdr
                    parameters = parameters.cdr
                }
                if (arguments != nil || parameters != nil) {
                    error("too few or too many arguments for lambda")
                }
                body.eval(bindings)
            } else {
                error("The first element of function call use be a function")
            }

        /**
         * 根据[cdr]不同的类型，展示不同的格式。
         */
        override fun toString(): String = StringBuilder("(").let { builder ->
            var cons: Cell = this
            while (true) {
                builder.append(cons.car)
                when (val cdr = cons.cdr) {
                    is Atom -> {
                        if (cdr != nil) {
                            builder.append(" . ").append(cdr)
                        }
                        break
                    }
                    is Cell -> {
                        builder.append(" ")
                        cons = cdr
                    }
                }
            }
            builder.append(")").toString()
        }

        /**
         * 返回原始表达式。
         */
        private fun quote(parameters: Expression): Expression =
            unary(parameters)

        /**
         * 执行第一个参数，并判断其是否为原子。
         */
        private fun atom(context: Bindings, parameters: Expression): Expression =
            unary(parameters).eval(context).isAtom()

        /**
         * 执行前两个参数，并判断它们是否相同。
         */
        private fun same(context: Bindings, parameters: Expression): Expression {
            val (left, right) = binary(parameters)
            return left.eval(context).eq(right.eval(context))
        }

        /**
         * 执行第一个参数，当它是[Cell]类型时，返回其[car]。
         */
        private fun car(context: Bindings, parameters: Expression): Expression =
            when (val parameter = unary(parameters).eval(context)) {
                nil -> nil
                is Cell -> parameter.car
                else -> error("car requires a cons cell")
            }

        /**
         * 执行第一个参数，当它是[Cell]类型时，返回其[cdr]。
         */
        private fun cdr(context: Bindings, parameters: Expression): Expression =
            when (val parameter = unary(parameters).eval(context)) {
                nil -> nil
                is Cell -> parameter.cdr
                else -> error("cdr requires a cons cell")
            }

        /**
         * 执行前两个参数，并将其组装成[Cell]。
         */
        private fun cons(context: Bindings, parameters: Expression): Expression {
            val (left, right) = binary(parameters)
            return Cell(left.eval(context), right.eval(context))
        }

        /**
         * 依次执行`(condition action)`对，返回第一个`condition`为真是的`action`的结果。
         */
        private fun cond(context: Bindings, parameters: Expression): Expression {
            var cons = parameters
            while (true) {
                if (cons is Cell) {
                    val (condition, action) = binary(cons.car)
                    cons = cons.cdr
                    if (condition.eval(context) != nil) {
                        return action.eval(context)
                    }
                } else if (cons == nil) {
                    break
                } else {
                    error("cond requires a (condition action) cons")
                }
            }
            return nil
        }

        /**
         * 确保[parameters]中只有1个参数，并读取该参数。
         */
        private fun unary(parameters: Expression): Expression =
            if (parameters is Cell && parameters.cdr == nil) {
                parameters.car
            } else {
                error("Requires one argument")
            }

        /**
         * 确保[parameters]中只有2个参数，并读取该参数。
         */
        private fun binary(parameters: Expression): Pair<Expression, Expression> =
            if (parameters is Cell && parameters.cdr is Cell && parameters.cdr.cdr == nil) {
                parameters.car to parameters.cdr.car
            } else {
                error("Requires two argument")
            }
    }

    /**
     * 是否为原子
     */
    fun isAtom(): Expression

    /**
     * 两个表达式是否为同一个原子。
     */
    fun eq(that: Expression): Expression

    /**
     * 在上下文中执行表达式。
     */
    fun eval(context: Bindings): Expression
}
