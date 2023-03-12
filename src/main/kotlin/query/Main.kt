package query

import bol.IndividualReference
import bol.Subset
import bol.Vocabulary
import bol.*



class Context(val voc:Vocabulary)
class Substitution(val defs: List<Pair<String,String>>)
abstract class Assertion()

class IsaAssertion(val c:ConceptReference):Assertion()

class QueryEngine(voc: Vocabulary) {
    private var inds: MutableList<String> = mutableListOf()
    private var concs: MutableList<String> = mutableListOf()
    private var subs : MutableList<Pair<String,String>> = mutableListOf()
     var isa: MutableList<Pair<String,String>> = mutableListOf()

    init {

        voc.decls.forEach {

            when(it) {
                is IndividualDec -> inds.add(it.id)
                is ConceptDec -> concs.add(it.id)
                is Axiom ->
                    when(it.f) {
                        is Isa -> isa.add(Pair((it.f.i as IndividualReference).id,(it.f.c as ConceptReference).id))
                        is Subset -> subs.add(Pair((it.f.c1 as ConceptReference).id,(it.f.c2 as ConceptReference).id))
                    }
            }
        }

        // cycle analysis of the concs-subs graph, throw out duplicates
        // then no cycles left

        var isChanged: Boolean
        do {
            isChanged = false

            val newItem = mutableListOf<Pair<String,String>>()

            for (sub in subs)
                for (pair in isa)
                    if (pair.second == sub.second) {
                        newItem.add(Pair(pair.first, sub.second))
                        isChanged = true
                    }
        }while (isChanged)



    }
}

// input: individual i1, individual i2; i1 isa teacher, i2 is course, i1 teaches i2
// output: i1 := FR, i2 := WuV
fun query(ctx: Context, goals: List<IsaAssertion>){
    // essentially a CSP:
    // individuals in ctx are the variables
    // domains is inds
    // constraints are goal

    val q = QueryEngine(ctx.voc)

    for (goal in goals) {

       val result =  q.isa.find { it.second == goal.c.id }

        if (result == null)
            println("Not found")
        else
            println(result.first)
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




    val goals = mutableListOf<IsaAssertion>()
    goals.add(IsaAssertion(ConceptReference("Student")))

    query(Context(v),goals)






}





