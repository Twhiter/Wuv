package morphism

import bol.*


class VocabularyMor(val assignments: List<Assignment>) : NonTerminal() {

}


abstract class NonTerminal()

abstract class Assignment : NonTerminal()


class IndividualAs(val id: String,val i: IndividualExp) : Assignment() {

}

class ConceptAs(val id: String,val c: ConceptExp) : Assignment() {

}

class RelationAs(val id: String,val r: RelationExp) :Assignment() {


}

class PropertyAs(val id: String, val type: Type,val p:PropertyExp) : Assignment() {

}

class AxiomAs(val id: String):Assignment()




object Checker {


    fun checkVocMor(domain: Vocabulary,coDomain:Vocabulary,mor:VocabularyMor): Boolean {
        val seenSoFar = mutableListOf<Assignment>()
        mor.assignments.forEach { assi ->
            if (!checkAssignment(domain,coDomain,assi, seenSoFar))
                return false
            seenSoFar.add(assi)
        }
        return true
    }


    fun checkAssignment(domain: Vocabulary,coDomain:Vocabulary,assignment: Assignment,seenSofar:List<Assignment>):Boolean {

       return when(assignment) {

            is IndividualAs -> {
                checkIndividualAs(domain, coDomain, assignment, seenSofar)
            }

            is ConceptAs -> {
                checkConceptAs(domain, coDomain, assignment, seenSofar)
            }

           is AxiomAs -> {
               checkAxiomAs(domain,coDomain,assignment,seenSofar)
           }


           else -> {throw Exception("not supported")}
       }
    }


    fun checkIndividualAs(domain: Vocabulary,coDomain:Vocabulary,assignment: IndividualAs,seenSofar:List<Assignment>):Boolean {

        val dec = domain.decls.find {
            it is IndividualDec && it.id == assignment.id
        } ?: return false

        return TypeChecker.checkIndividualExp(assignment.i, coDomain.decls)
    }

    fun checkConceptAs(domain: Vocabulary,coDomain:Vocabulary,assignment: ConceptAs,seenSofar:List<Assignment>):Boolean {


        val dec = domain.decls.find {
            it is ConceptDec && it.id == assignment.id
        } ?: return false

        return TypeChecker.checkConceptExp(assignment.c,coDomain.decls)
    }

    fun checkAxiomAs(domain: Vocabulary,coDomain:Vocabulary,assignment: AxiomAs,seenSofar:List<Assignment>):Boolean {
        val axiom = domain.decls.find {
            it is Axiom && it.id == assignment.id
        } ?: return false
        val proofObligation = homomorphicExtensionF(VocabularyMor(seenSofar),(axiom as Axiom).f)
        println("Prove: ${proofObligation}")
        return true
    }


    fun homomorphicExtensionC(mor:VocabularyMor,exp: ConceptExp):ConceptExp {

        return when(exp) {
            is Union -> {
                Union(homomorphicExtensionC(mor,exp.c1), homomorphicExtensionC(mor,exp.c2))
            }

            is ConceptReference -> {
                val result = mor.assignments.find { it is ConceptAs && it.id == exp.id }
                    ?: throw Exception("Morphism must be well formed !")

                (result as ConceptAs).c
            }

            else -> throw Exception("not supported")
        }
    }

    fun homomorphicExtensionI(mor:VocabularyMor,exp: IndividualExp):IndividualExp {

        return when(exp) {

            is IndividualReference -> {

                val result = mor.assignments.find { it is IndividualAs && it.id == exp.id }
                    ?: throw Exception("Morphism must be well formed!")

                (result as IndividualAs).i
            }
            else -> throw Exception("not supported")
        }
    }


    fun homomorphicExtensionF(mor: VocabularyMor,exp:Formula):Formula {





        return when(exp) {

            is Equal -> {
                Equal(homomorphicExtensionC(mor,exp.c1), homomorphicExtensionC(mor,exp.c2))
            }

            is Subset -> {
                Subset(homomorphicExtensionC(mor,exp.c1), homomorphicExtensionC(mor,exp.c2))
            }

            is Isa -> {
                Isa(homomorphicExtensionI(mor,exp.i), homomorphicExtensionC(mor,exp.c))
            }


            else -> {throw Exception("not supported")}
        }
    }

}

fun main() {



    val man = ConceptDec("man")
    val woman = ConceptDec("woman")
    val A = IndividualDec("A")
    val axiom = Axiom(Isa(IndividualReference("A"),ConceptReference("man")))

    val v1 = Vocabulary(listOf(man,woman,A,axiom))




    val cisman = ConceptDec("cisman")
    val transman = ConceptDec("transman")
    val ciswoman = ConceptDec("ciswoman")
    val transwoman = ConceptDec("transwoman")

    val v2 = Vocabulary(listOf(cisman,transman,ciswoman,transwoman))



    val manAs = ConceptAs("man",Union(ConceptReference("cisman"),ConceptReference("transman")))
    val womanAs = ConceptAs("woman",Union(ConceptReference("ciswoman"),ConceptReference("transwoman")))


    val mor = VocabularyMor(listOf(manAs,womanAs))

    println(Checker.checkVocMor(v1, v2, mor))


}