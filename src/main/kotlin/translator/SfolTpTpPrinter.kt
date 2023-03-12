package translator

import bol.*
import bol.IntValue
import sfol.*
import sfol.Axiom
import sfol.Declaration
import sfol.Vocabulary


fun makeTft(id: String, body: String, type: String = "axiom"): String {
    return "tff($id,$type,$body)."
}

var count:Int = 0


object SfolTpTpPrinter {

    fun printVocabulary(voc: Vocabulary): String {
        return voc.decls.map(SfolTpTpPrinter::printDeclaration)
            .filter { it != "" }
            .joinToString(separator = "\n")
    }


    private fun printDeclaration(decl: Declaration) = when (decl) {
        is Axiom -> makeTft("axiom_${count++}", printFormula(decl.f))
        else -> ""
    }

    private fun printFormula(f: FormulaExpression): String {

        return when (f) {

            is PredicateInvoke -> {
                if (f.params.isEmpty()) f.id
                else "${f.id}(${f.params.joinToString(transform = SfolTpTpPrinter::printTermExpression)})"
            }
            is TermEquality -> "${printTermExpression(f.t1)} = ${printTermExpression(f.t2)}"
            is Truth -> "1=1"
            is False -> "~1=1"
            is Conjunction -> "(${printFormula(f.f1)} & ${printFormula(f.f2)})"
            is Disjunction -> "(${printFormula(f.f1)} | ${printFormula(f.f2)})"
            is Implication -> "(${printFormula(f.f1)} => ${printFormula(f.f2)})"
            is Equivalence -> "(${printFormula(f.f1)} <=> ${printFormula(f.f2)})"
            is Negation -> "~(${printFormula(f.f)})"
            is Universal -> "! [${f.id}]:(${printFormula(f.f)})"
            is Existential -> "? [${f.id}]:(${printFormula(f.f)})"

            else -> throw Exception("${f.javaClass.name} is not supported")
        }
    }


    private fun printTermExpression(t: TermExpression): String {

        return when (t) {
            is FunctionInvoke -> {
                if (t.params.isEmpty()) t.id
                else "f_${t.id}(${t.params.joinToString(transform = SfolTpTpPrinter::printTermExpression)})"
            }

            is TermVariable -> t.id

            else -> throw Exception("${t.javaClass.name} is not supported")
        }
    }

}


fun main() {


    val i1 = IndividualDec("florian")
    val i2 = IndividualDec("tu")
    val i3 = IndividualDec("wuV")

    val c1 = ConceptDec("lecturer")
    val c2 = ConceptDec("student")
    val c3 = ConceptDec("course")
    val c4 = ConceptDec("teacher")


    val r1 = RelationDec("teach")
    val r2 = RelationDec("study")

    val p = PropertyDec("age", Integer())
    val p1 = PropertyDec("semester", Semester())

    val cAssert1 = bol.Axiom(Isa(IndividualReference("florian"), ConceptReference("lecturer")))
    val cAssert2 = bol.Axiom(Isa(IndividualReference("tu"), ConceptReference("student")))
    val cAssert3 = bol.Axiom(Isa(IndividualReference("wuV"), ConceptReference("course")))

    val pAssert1 = bol.Axiom(PropertyAssertion(IndividualReference("tu"), PropertyReference("age"), IntValue(22)))
    val pAssert2 = bol.Axiom(PropertyAssertion(IndividualReference("florian"), PropertyReference("age"), IntValue(40)))
    val pAssert3 =
        bol.Axiom(PropertyAssertion(IndividualReference("wuV"), PropertyReference("semester"), SemesterValue(22, true)))


    val rAssert1 = bol.Axiom(
        RelationAssertion(
            IndividualReference("florian"), RelationReference("teach"),
            IndividualReference("wuV")
        )
    )

    val rAssert2 = bol.Axiom(
        RelationAssertion(
            IndividualReference("tu"), RelationReference("study"), IndividualReference("wuV")
        )
    )

    val c5 = ConceptDec("male")
    val cAssert4 = bol.Axiom(Isa(IndividualReference("florian"), ConceptReference("male")))
    val cAssert5 = bol.Axiom(Isa(IndividualReference("tu"), ConceptReference("male")))

    val conceptTemp = Intersect(ConceptReference("student"),Domain(RelationReference("study")))
    val conceptTemp2 = Intersect(conceptTemp,ConceptReference("male"))
    val axiom = bol.Axiom(Equal(conceptTemp2,ConceptReference("student")))









    val v = bol.Vocabulary(
        listOf(
            i1,
            i2,
            i3,
            c1,
            c2,
            c3,
            c4,
            r1,
            r2,
            p,
            p1,
            cAssert1,
            cAssert2,
            cAssert3,
            pAssert1,
            pAssert2,
            pAssert3,
            rAssert1,
            rAssert2,
            axiom
        )
    )

    println(v.print())
    println()

    val v_ = FromBolToSfol.translateVocabulary(v)
    println(v_.print())
    println()





    println(SfolTpTpPrinter.printVocabulary(v_))


}