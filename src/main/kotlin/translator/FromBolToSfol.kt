package translator

import bol.*
import bol.Axiom
import bol.Declaration
import bol.IntValue
import bol.StringValue
import bol.Value
import bol.Vocabulary
import sfol.*
import java.util.*

object FromBolToSfol {

    var count = 0


    lateinit var voc: Vocabulary

    fun translateVocabulary(v: Vocabulary): sfol.Vocabulary {
        voc = v
        val decls = v.decls.map { translateDeclaration(it) }
        return sfol.Vocabulary(decls)
    }

    fun translateDeclaration(d: Declaration): sfol.Declaration {

        return when (d) {
            is IndividualDec -> FunctionDeclaration(d.id, emptyList(), IOTA)
            is ConceptDec -> PredicateDeclaration(d.id, listOf(IOTA))
            is RelationDec -> PredicateDeclaration(d.id, listOf(IOTA, IOTA))
            is PropertyDec -> PredicateDeclaration(d.id, listOf(IOTA))
            is Axiom -> sfol.Axiom(translateFormula(d.f))

            else -> throw Exception()
        }
    }

    fun translateFormula(f: Formula): FormulaExpression {

        return when (f) {

            is Equal -> {
                val x = TermVariable("X_${count++}")
                Universal(
                    x.id, IOTA, Equivalence(
                        translateConceptExpression(f.c1, x),
                        translateConceptExpression(f.c2, x)
                    )
                )
            }

            is Subset -> {
                val x = TermVariable("X_${count++}")
                Universal(
                    x.id, IOTA, Implication(
                        translateConceptExpression(f.c1, x),
                        translateConceptExpression(f.c2, x)
                    )
                )
            }

            is Isa ->
                translateConceptExpression(f.c, translateIndivExpression(f.i))

            is RelationAssertion -> translateRelationExpression(
                f.r, translateIndivExpression(f.i1),
                translateIndivExpression(f.i2)
            )

            is PropertyAssertion -> translatePropertyExpression(f.p, translateIndivExpression(f.i), translateValue(f.v))
            else -> throw Exception("${f.javaClass.name} is not supported")
        }
    }

    fun translateIndivExpression(i: IndividualExp): FunctionInvoke {

        return when (i) {
            is IndividualReference -> FunctionInvoke(i.id, emptyList())
            else -> throw Exception("${i.javaClass.name} is not supported")
        }
    }

    fun translateConceptExpression(c: ConceptExp, x: TermExpression): FormulaExpression {

        return when (c) {
            is ConceptReference -> PredicateInvoke(c.id, listOf(x))
            is UniversalConcept -> Truth()
            is EmptyConcept -> False()
            is Union -> Disjunction(translateConceptExpression(c.c1, x), translateConceptExpression(c.c2, x))
            is Intersect -> Conjunction(translateConceptExpression(c.c1, x), translateConceptExpression(c.c2, x))
            is UniversalRel -> {
                val y = TermVariable("Y_${count++}")
                Universal(
                    y.id,
                    IOTA,
                    Implication(translateRelationExpression(c.r, x, y), translateConceptExpression(c.c, y))
                )
            }

            is ExistentialRel -> {
                val y = TermVariable("Y_${count++}")
                Existential(
                    y.id,
                    IOTA,
                    Conjunction(translateRelationExpression(c.r, x, y), translateConceptExpression(c.c, y))
                )
            }

            is Domain -> {
                val y = TermVariable("Y_${count++}")
                Existential(y.id, IOTA, translateRelationExpression(c.r, x, y))
            }

            is Range -> {
                val y = TermVariable("Y_${count++}")
                Existential(y.id, IOTA, translateRelationExpression(c.r, y, x))
            }

            is PropertyDomain ->
                when (c.p) {
                    is PropertyReference -> {
                        //look up the property definition and get
                        val pDecl = voc.decls.find { it is PropertyDec && it.id == c.p.id }!! as PropertyDec
                        val y = TermVariable("Y_${count++}")
                        Existential(y.id, translateType(pDecl.type), translatePropertyExpression(c.p, x, y))
                    }

                    else -> throw Exception("${c.javaClass.name} is not supported")
                }


            else -> throw Exception("${c.javaClass.name} is not supported")
        }
    }

    fun translateRelationExpression(r: RelationExp, x: TermExpression, y: TermExpression): FormulaExpression {

        return when (r) {
            is RelationReference -> PredicateInvoke(r.id, listOf(x, y))
            is RelationUnion -> Disjunction(
                translateRelationExpression(r.r1, x, y),
                translateRelationExpression(r.r2, x, y)
            )

            is RelationIntersect -> Conjunction(
                translateRelationExpression(r.r1, x, y),
                translateRelationExpression(r.r2, x, y)
            )

            is RelationComposition -> {
                val m = TermVariable("m_" + Random().nextInt(100))
                Existential(
                    m.id, IOTA, Conjunction(
                        translateRelationExpression(r.r1, x, m),
                        translateRelationExpression(r.r2, m, y)
                    )
                )
            }

            is RelationDual -> translateRelationExpression(r.r, y, x)
            is RelationClosure -> throw Exception("not support")
            is ConceptIdeRelation -> Conjunction(TermEquality(x, y), translateConceptExpression(r.c, x))

            else -> throw Exception("${r.javaClass.typeName} is not supported")
        }

    }

    fun translatePropertyExpression(p: PropertyExp, x: TermExpression, y: TermExpression) = when (p) {
        is PropertyReference -> PredicateInvoke(p.id, listOf(x, y))
        else -> throw Exception("${p.javaClass.name} is not supported")
    }


    fun translateType(t: Type): TypeExpression {

        return when(t) {
            is Integer -> integer
            is Str -> string
            is Semester -> semester
            else -> throw Exception("${t.javaClass.name} is not supported")
        }
    }

    fun translateValue(v: Value): TermExpression {
        return when (v) {
            is StringValue -> TermVariable("\"${v.value}\"")
            is IntValue -> TermVariable(v.value.toString())
            is SemesterValue -> TermVariable("\"${v.print()}\"")
            else -> throw Exception("${v.javaClass.name} is not supported")
        }
    }
}





fun main() {



    val i1 = IndividualDec("Florian")
    val i2 = IndividualDec("Tu")
    val i3 = IndividualDec("WuV")

    val c1 = ConceptDec("Lecturer")
    val c2 = ConceptDec("Student")
    val c3 = ConceptDec("Course")
    val c4 = ConceptDec("Teacher")


    val r1 = RelationDec("Teach")
    val r2 = RelationDec("Study")

    val p = PropertyDec("Age",Integer())
    val p1 = PropertyDec("Semester",Semester())

    val cAssert1 = Axiom(Isa(IndividualReference("Florian"),ConceptReference("Lecturer")))
    val cAssert2 = Axiom(Isa(IndividualReference("Tu"),ConceptReference("Student")))
    val cAssert3 = Axiom(Isa(IndividualReference("WuV"),ConceptReference("Course")))

    val pAssert1 = Axiom(PropertyAssertion(IndividualReference("Tu"),PropertyReference("Age"),IntValue(22)))
    val pAssert2 = Axiom(PropertyAssertion(IndividualReference("Florian"),PropertyReference("Age"),IntValue(40)))
    val pAssert3 = Axiom(PropertyAssertion(IndividualReference("WuV"),PropertyReference("Semester"),SemesterValue(22,true)))


    val rAssert1 = Axiom(RelationAssertion(IndividualReference("Florian"),RelationReference("Teach"),
        IndividualReference("WuV")))

    val rAssert2 = Axiom(RelationAssertion(IndividualReference("Tu"),RelationReference("Study")
        ,IndividualReference("WuV")))

    val v = Vocabulary(listOf(i1,i2,i3,c1,c2,c3,c4,r1,r2,p,p1,cAssert1,cAssert2,cAssert3,pAssert1,pAssert2
        ,pAssert3,rAssert1,rAssert2))

    println(v.print())
    println()


    val v_ = FromBolToSfol.translateVocabulary(v)
    println(v_.print())


}