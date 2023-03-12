package sfol

abstract class NonTerminal {
    abstract fun print():String
}


class Vocabulary(val decls:List<Declaration>) : NonTerminal() {
    override fun print() = decls.joinToString(separator = "\n") { it.print() }
}

abstract class Declaration : NonTerminal()
abstract class TypeExpression : NonTerminal()
abstract class TermExpression : NonTerminal()
abstract class FormulaExpression : NonTerminal()


object IOTA:TypeExpression() {
    override fun print() = "Individual"
}

val string = AtomicType("String")
val integer = AtomicType("Int")
val semester = AtomicType("Semester")



class TypeDeclaration(val id: String) : Declaration() {
    override fun print() = "type $id"
}

class FunctionDeclaration(val id: String, val paramType: List<TypeExpression>, val outputType: TypeExpression) :
    Declaration() {
    override fun print() = "fun $id (${paramType.joinToString(separator = ",") {it.print()}}) -> ${outputType.print()} "
}

class PredicateDeclaration(val id: String, val types: List<TypeExpression>) : Declaration() {
    override fun print() = "Pred $id (${types.joinToString(separator = ",") {it.print()}}) "
}

class Axiom(val f: FormulaExpression) : Declaration() {
    override fun print() = "Axiom ${f.print()}"
}

class AtomicType(val id: String) : TypeExpression() {
    override fun print() = id
}

class FunctionInvoke(val id: String, val params:List<TermExpression>):TermExpression() {
    override fun print() = "$id(${params.joinToString(separator = ",") { it.print() }})"
}

class TermVariable(val id:String):TermExpression() {
    override fun print() = id
}

class PredicateInvoke(val id: String,val params: List<TermExpression>):FormulaExpression() {
    override fun print() = "$id(${params.joinToString(separator = ",") { it.print() }})"
}

class TermEquality(val t1:TermExpression, val t2:TermExpression):FormulaExpression() {
    override fun print() = "${t1.print()} = ${t2.print()}"
}

class Truth():FormulaExpression() {
    override fun print() = "⊤"
}

class False():FormulaExpression() {
    override fun print() = "⊥"
}

class Conjunction(val f1: FormulaExpression,val f2:FormulaExpression):FormulaExpression() {
    override fun print() = "${f1.print()} ∧ ${f2.print()}"
}

class Disjunction(val f1:FormulaExpression,val f2:FormulaExpression):FormulaExpression() {
    override fun print() = "${f1.print()} V ${f2.print()}"
}

class Implication(val f1:FormulaExpression,val f2:FormulaExpression):FormulaExpression() {
    override fun print() = "${f1.print()} ⇒ ${f2.print()}"
}

class Equivalence(val f1:FormulaExpression,val f2:FormulaExpression):FormulaExpression() {
    override fun print() = "${f1.print()} ⇔ ${f2.print()}"
}

class Negation(val f:FormulaExpression):FormulaExpression() {
    override fun print() = "¬ ${f.print()}"
}

class Universal(val id:String,val type:TypeExpression,val f:FormulaExpression):FormulaExpression() {
    override fun print() = "∀$id:${type.print()} ${f.print()}"
}

class Existential(val id: String,val type: TypeExpression,val f: FormulaExpression):FormulaExpression() {
    override fun print() = "∃$id:${type.print()} ${f.print()}"
}


abstract class Value:NonTerminal()

class StringValue(val v:String):Value() {
    override fun print() = "\"$v\""
}

class IntValue(val v:Int):Value() {
    override fun print() = v.toString()
}
