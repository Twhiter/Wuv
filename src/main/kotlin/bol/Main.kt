package bol
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.math.BigDecimal
import java.math.BigInteger
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.reflect.full.declaredMemberProperties


abstract class NonTerminal : XMLSerializable {
    abstract fun print(): String
}

interface XMLSerializable {
    fun serializeToXml(): String
}

class Vocabulary(val decls: List<Declaration>) : NonTerminal() {
    override fun print(): String = decls.joinToString(separator = "\n") { it.print() }
    override fun serializeToXml(): String = """
        <vocabulary>  
            ${decls.joinToString(separator = "", transform = XMLSerializable::serializeToXml)}
        </vocabulary>"""
}









abstract class Declaration : NonTerminal()
abstract class Formula : NonTerminal()
abstract class Type : NonTerminal()
abstract class ConceptExp : NonTerminal()
abstract class RelationExp : NonTerminal()
abstract class PropertyExp : NonTerminal()
abstract class IndividualExp : NonTerminal()

abstract class Value : NonTerminal()















class IndividualDec(val id: String) : Declaration() {
    override fun print(): String = "Individual $id"
    override fun serializeToXml(): String = """
        <inddecl name="${id}"/>"""
}

class ConceptDec(val id: String) : Declaration() {
    override fun print(): String = "Concept $id"
    override fun serializeToXml(): String = """
        <concdecl name="${id}"/>"""
}

class RelationDec(val id: String) : Declaration() {
    override fun print(): String = "Relation $id"
    override fun serializeToXml(): String = """
        <reldecl name="${id}"/>"""

}

class PropertyDec(val id: String, val type: Type) : Declaration() {
    override fun print(): String = "property ${id} ${type.print()}"
    override fun serializeToXml(): String = """
        <propdecl name="${id}">
            ${type.serializeToXml()}
        </propdecl>
        """

}

class Axiom(val f:Formula,val id: String = ""):Declaration() {
    override fun print() = "Axiom ${f.print()}"

    override fun serializeToXml() = """
        <axiom>
            ${f.serializeToXml()}
        </axiom>
    """.trimIndent()

}

class Isa(val i: IndividualExp, val c: ConceptExp) : Formula() {
    override fun print(): String = i.print() + "is a" + c.print()
    override fun serializeToXml(): String = """
            <concassert>
                ${i.serializeToXml()}
                ${c.serializeToXml()}
            </concassert>"""
}

class RelationAssertion(val i1: IndividualExp, val r: RelationExp, val i2: IndividualExp) : Formula() {
    override fun print(): String = "${i1.print()} ${r.print()} ${i2.print()}"
    override fun serializeToXml(): String = """
            <relassert>
                ${i1.serializeToXml()}
                ${r.serializeToXml()}
                ${i2.serializeToXml()}
            </relassert >"""

}

class PropertyAssertion(val i: IndividualExp, val p: PropertyExp, val v: Value) : Formula() {
    override fun print(): String = "${i.print()} ${p.print()} ${v.print()}"
    override fun serializeToXml(): String = """
            <propassert>
                ${i.serializeToXml()}
                ${p.serializeToXml()}
                ${v.serializeToXml()}
            </propassert>"""

}

class Equal(val c1: ConceptExp, val c2: ConceptExp) : Formula() {
    override fun print(): String = c1.print() + "==" + c2.print()
    override fun serializeToXml(): String = """
            <conceq>
                ${c1.serializeToXml()}
                ${c2.serializeToXml()}
            </conceq>"""

}

class Subset(val c1: ConceptExp, val c2: ConceptExp) : Formula() {
    override fun print(): String = c1.print() + "belongs to " + c2.print()
    override fun serializeToXml(): String = """
            <concsubs>
                ${c1.serializeToXml()}
                ${c2.serializeToXml()}
            </concsubs>"""

}

class IndividualReference(val id: String) : IndividualExp() {
    override fun print(): String = "Individual $id"
    override fun serializeToXml(): String = """
        <indref name="$id"  />"""

}

class ConceptReference(val id: String) : ConceptExp() {
    override fun print(): String = "Concept $id"
    override fun serializeToXml(): String = """
        <concref name="$id"/>"""

}

class UniversalConcept():ConceptExp() {
    override fun print() = "Any Concept"
    override fun serializeToXml(): String = "\n<concuniversal/>"

}

class EmptyConcept():ConceptExp() {
    override fun print() = "Empty Concept"
    override fun serializeToXml(): String = "\n<concempty/>"


}

class Union(val c1: ConceptExp, val c2: ConceptExp) : ConceptExp() {
    override fun print(): String = "${c1.print()} U ${c2.print()}"
    override fun serializeToXml(): String = """
        <concunion>
            ${c1.serializeToXml()}
            ${c2.serializeToXml()}
        </concunion>"""

}

class Intersect(val c1: ConceptExp, val c2: ConceptExp) : ConceptExp() {
    override fun print(): String = "${c1.print()} n ${c2.print()}"
    override fun serializeToXml(): String = """
        <concintersec>
            ${c1.serializeToXml()}
            ${c2.serializeToXml()}
        </concintersec>"""


}

class UniversalRel(val r:RelationExp, val c:ConceptExp):ConceptExp() {
    override fun print(): String = "for all ${r.print()}.${c.print()}"

    override fun serializeToXml(): String = """
         <concuniversalrel>
            ${r.serializeToXml()}
            ${c.serializeToXml()}
         </concuniversalrel>"""


}

class ExistentialRel(val r:RelationExp, val c:ConceptExp):ConceptExp() {
    override fun print(): String = "Exist ${r.print()}.${c.print()}"

    override fun serializeToXml(): String = """
       <concexistlrel>
         ${r.serializeToXml()}
         ${c.serializeToXml()}
       </concexistlrel>"""

}


class Domain(val r: RelationExp) : ConceptExp() {
    override fun print(): String = "domain ${r.print()}"
    override fun serializeToXml(): String = """
        <concdomainrel>
            ${r.serializeToXml()}
        </concdomainrel>"""

}

class Range(val r: RelationExp) : ConceptExp() {
    override fun print(): String = "range ${r.print()}"
    override fun serializeToXml(): String = """
        <concrangerel>
            ${r.serializeToXml()}
        </concrangerel>"""

}

class PropertyDomain(val p: PropertyExp) : ConceptExp() {
    override fun print(): String = "property domain ${p.print()}"
    override fun serializeToXml(): String = """
        <concdomainprop>
            ${p.serializeToXml()}
        </concdomainprop>"""

}

class RelationReference(val id: String) : RelationExp() {
    override fun print(): String = "relation $id"
    override fun serializeToXml(): String = """
        <relref name="$id"/>"""
}

class RelationUnion(val r1: RelationExp, val r2: RelationExp) : RelationExp() {
    override fun print(): String = "${r1.print()} U ${r2.print()}"
    override fun serializeToXml(): String = """
        <relunion>
            ${r1.serializeToXml()}
            ${r2.serializeToXml()}
        </relunion>"""
}

class RelationIntersect(val r1: RelationExp, val r2: RelationExp) : RelationExp() {
    override fun print(): String = "${r1.print()} n ${r2.print()}"
    override fun serializeToXml(): String = """
        <relintersec>
            ${r1.serializeToXml()}
            ${r2.serializeToXml()}
        </relintersec>"""

}

class RelationComposition(val r1:RelationExp,val r2:RelationExp): RelationExp() {
    override fun print() = "${r1.print()};${r2.print()}"

    override fun serializeToXml(): String = """
       <relcomp>
         ${r1.serializeToXml()}
         ${r2.serializeToXml()}
       </relcomp>"""

}

class RelationClosure(val r:RelationExp):RelationExp() {
    override fun print() = "${r.print()}*"
    override fun serializeToXml(): String = """
       <reltrans>
         ${r.serializeToXml()}
       </reltrans>"""

}

class RelationDual(val r:RelationExp):RelationExp() {
    override fun print() = "${r.print()}^-1"

    override fun serializeToXml(): String = """
        <relinv>
            ${r.serializeToXml()}
        </relinv>
    """

}

class ConceptIdeRelation(val c:ConceptExp):RelationExp() {
    override fun print() = "(${c.print()},${c.print()})"
    override fun serializeToXml() = """
        <relident>
            ${c.serializeToXml()}
        </relident>
    """

}

class PropertyReference(val id: String) : PropertyExp() {
    override fun print(): String = "property $id"
    override fun serializeToXml(): String = """
        <propref name="$id"/>"""

}

class Integer : Type() {
    override fun print(): String = "Int"
    override fun serializeToXml(): String = """
            <basetype name="Int"/>"""

}

class Str : Type() {
    override fun print(): String = "String"
    override fun serializeToXml(): String = """
        <basetype name="String"/>"""

}

class BigInt: Type() {
    override fun print(): String = "BigInt"

    override fun serializeToXml(): String = """
        <basetype name="BigInt"/>"""

}

class Bool: Type() {
    override fun print(): String = "Bool"

    override fun serializeToXml(): String = """
        <basetype name="Bool"/>"""

}

class Date: Type() {
    override fun print(): String = "Date"

    override fun serializeToXml(): String = """
        <basetype name="Date"/>"""

}

class Grade: Type() {
    override fun print(): String = "Grade"

    override fun serializeToXml(): String = """
        <basetype name="Grade"/>"""

}

class Semester:Type() {
    override fun print(): String = "Semester"

    override fun serializeToXml(): String = """
        <basetype name="Semester"/>
    """

}

class IntValue(val value: Int) : Value() {
    override fun print(): String = value.toString()
    override fun serializeToXml(): String = """
        <basevalue value="${value}" typename="Int"/>"""

}

class StringValue(val value: String) : Value() {
    override fun print(): String = value
    override fun serializeToXml(): String = """
        <basevalue value="${value}" typename="String"/>"""

}

class BigIntValue(val value:BigInteger):Value() {
    override fun print(): String = value.toString()

    override fun serializeToXml(): String = """
        <basevalue value="${value}" typename="BigInt"/>
    """

}

class BoolValue(val value: Boolean):Value() {
    override fun print(): String = value.toString()

    override fun serializeToXml(): String = """
        <basevalue value="${value}" typename="Bool"/>
    """

}

class DateValue(val timestamp:String):Value() {

    override fun print(): String = timestamp
    override fun serializeToXml(): String = """
        <basevalue value="${timestamp}" typename="Date"/>
    """

}

class GradeValue(val value:BigDecimal):Value() {
    override fun print(): String = value.toString()

    override fun serializeToXml(): String = """
        <basevalue value="${value.setScale(2)}" typename="Grade"/>"""

}

class SemesterValue(val year:Int,val isWinter:Boolean):Value() {
    override fun print(): String = if (isWinter) "$year/${year + 1}WS" else "${year}SS"

    override fun serializeToXml(): String = """
        <basevalue value="${print()}" typename="Semester"/>"""

}


object TypeChecker {

    fun checkVoc(vocabulary: Vocabulary): Boolean {
        val seenSoFar = mutableListOf<Declaration>()
        vocabulary.decls.forEach { d ->
            if (!checkDeclaration(d, seenSoFar))
                return false
            seenSoFar.add(d)
        }
        return true
    }




    public fun checkDeclaration(d: Declaration, seenSoFar: List<Declaration>): Boolean {
        return when (d) {
            is IndividualDec, is ConceptDec, is RelationDec, is PropertyDec -> {
                val id: String = getProperty(d, "id")
                seenSoFar.find {
                    (it is IndividualDec || it is ConceptDec || it is RelationDec || it is PropertyDec) &&
                            (getProperty<String>(it, "id") == id)
                } == null
            }

            is Axiom -> checkFormula(d.f, seenSoFar)
            else -> throw Exception("Unknown Declaration class: ${d.javaClass}")
        }
    }

    public fun checkFormula(f: Formula, seenSoFar: List<Declaration>): Boolean {
        return when (f) {
            is Isa ->
                checkIndividualExp(f.i, seenSoFar) && checkConceptExp(f.c, seenSoFar)

            is RelationAssertion -> {
                checkIndividualExp(f.i1, seenSoFar) && checkRelationExp(f.r, seenSoFar)
                        && checkIndividualExp(f.i2, seenSoFar)
            }

            is PropertyAssertion -> {
                if (!checkIndividualExp(f.i, seenSoFar)) return false
                val propDec = checkPropertyExp(f.p, seenSoFar) ?: return false
                return checkValue(propDec.type,f.v)
            }

            is Equal ->
                checkConceptExp(f.c1, seenSoFar) && checkConceptExp(f.c2, seenSoFar)

            is Subset ->
                checkConceptExp(f.c1, seenSoFar) && checkConceptExp(f.c2, seenSoFar)

            else -> throw Exception("Unknown Formula class: ${f.javaClass}")
        }
    }

    public fun checkIndividualExp(i: IndividualExp, seenSoFar: List<Declaration>): Boolean {
        return when (i) {
            is IndividualReference ->
                seenSoFar.find { it is IndividualDec && it.id == i.id } != null

            else -> throw Exception("Unknown Individual Expression class:${i.javaClass}")
        }
    }

    public fun checkConceptExp(c: ConceptExp, seenSoFar: List<Declaration>): Boolean {
        return when (c) {
            is ConceptReference ->
                seenSoFar.find { it is ConceptDec && it.id == c.id } != null

            is UniversalConcept,is EmptyConcept -> true

            is Union ->
                checkConceptExp(c.c1, seenSoFar) && checkConceptExp(c.c2, seenSoFar)

            is Intersect ->
                checkConceptExp(c.c1, seenSoFar) && checkConceptExp(c.c2, seenSoFar)

            is UniversalRel ->
                checkRelationExp(c.r,seenSoFar) && checkConceptExp(c.c,seenSoFar)

            is ExistentialRel ->
                checkRelationExp(c.r,seenSoFar) && checkConceptExp(c.c,seenSoFar)

            is Domain -> checkRelationExp(c.r,seenSoFar)
            is Range -> checkRelationExp(c.r,seenSoFar)
            else ->
                throw Exception("Unknown concept expression class: ${c.javaClass}")
        }
    }

    public fun checkRelationExp(r: RelationExp, seenSoFar: List<Declaration>): Boolean {
        return when (r) {
            is RelationReference ->
                seenSoFar.find { it is RelationDec && it.id == r.id } != null

            is RelationUnion ->
                checkRelationExp(r.r1, seenSoFar) && checkRelationExp(r.r2, seenSoFar)

            is RelationIntersect ->
                checkRelationExp(r.r1, seenSoFar) && checkRelationExp(r.r2, seenSoFar)

            is RelationComposition ->
                checkRelationExp(r.r1,seenSoFar) && checkRelationExp(r.r2,seenSoFar)

            is RelationClosure -> checkRelationExp(r.r,seenSoFar)
            is ConceptIdeRelation -> checkConceptExp(r.c,seenSoFar)
            else ->
                throw Exception("Unknown relation expression class: ${r.javaClass}")
        }
    }

    public fun checkPropertyExp(p: PropertyExp, seenSoFar: List<Declaration>): PropertyDec? {

        return when (p) {
            is PropertyReference ->
                seenSoFar.find { it is PropertyDec && p.id == it.id } as PropertyDec?
            else -> throw Exception("Unknown Property Expression class: ${p.javaClass}")
        }
    }


    //true if the ${value} 's type corresponds to the ${type}
    private fun checkValue(type: Type,value:Value): Boolean = when(type) {
        is Str ->  value is StringValue
        is Integer ->  value is IntValue
        is BigInt ->  value is BigIntValue
        is Bool ->  value is BoolValue
        is Date ->  value is DateValue
        is Grade ->  value is GradeValue
        is Semester ->  value is SemesterValue
        else -> throw Exception("Unexpected type class: ${type.javaClass}")
    }

}

object IdtParser {

    fun parse(s:String): Vocabulary {

        val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(s.byteInputStream())
        xmlDoc.normalize()
        return parseVocabulary(xmlDoc.documentElement)
    }


    private fun parseVocabulary(e: Element): Vocabulary {
        val children = getChildElement(e)
        val decls = children.map(IdtParser::parseDeclaration)
        return Vocabulary(decls)
    }

    private fun parseDeclaration(e: Element): Declaration {
        val c = getChildElement(e)
        return when (e.nodeName) {
            "inddecl" -> IndividualDec(e.getAttribute("name"))
            "concdecl" -> ConceptDec(e.getAttribute("name"))
            "reldecl" -> RelationDec(e.getAttribute("name"))
            "propdecl" -> PropertyDec(e.getAttribute("name"), parseType(c[0]))

            "axiom" -> Axiom(parseFormula(c[0]))

            else -> throw Exception("${e.nodeName} can't be converted to declaration")
        }
    }

    private fun parseFormula(e: Element): Formula {

        val c = getChildElement(e)
        return when (e.nodeName) {
            "concassert" -> Isa(parseIndividualExp(c[0]), parseConceptExp(c[1]))

            "propassert" -> PropertyAssertion(
                parseIndividualExp(c[0]), parsePropertyExp(c[1]),
                parseValue(c[2])
            )

            "relassert" -> RelationAssertion(
                parseIndividualExp(c[0]), parseRelationExp(c[1]),
                parseIndividualExp(c[2])
            )

            "concsubs" -> Subset(parseConceptExp(c[0]), parseConceptExp(c[1]))
            "conceq" -> Equal(parseConceptExp(c[0]), parseConceptExp(c[1]))

            else -> throw Exception("${e.nodeName} can't be converted to formula")
        }
    }

    private fun parseConceptExp(e: Element): ConceptExp {

        val c = getChildElement(e)
        return when (e.nodeName) {
            "concref" -> ConceptReference(e.getAttribute("name"))
            "concunion" -> Union(parseConceptExp(c[0]), parseConceptExp(c[1]))
            "concintersec" -> Intersect(parseConceptExp(c[0]), parseConceptExp(c[1]))
            "concuniversal" -> UniversalConcept()
            "concempty" -> EmptyConcept()
            "concuniversalrel" -> UniversalRel(parseRelationExp(c[0]), parseConceptExp(c[1]))
            "concexistlrel" -> ExistentialRel(parseRelationExp(c[0]), parseConceptExp(c[1]))
            "concdomainrel" -> Domain(parseRelationExp(c[0]))
            "concrangerel" -> Range(parseRelationExp(c[0]))
            "concdomainprop" -> PropertyDomain(parsePropertyExp(c[0]))

            else -> throw Exception("${e.nodeName} can't be converted to concept")
        }

    }

    private fun parseRelationExp(e: Element): RelationExp {
        val c = getChildElement(e)
        return when (e.nodeName) {
            "relref" -> RelationReference(e.getAttribute("name"))
            "relunion" -> RelationUnion(parseRelationExp(c[0]), parseRelationExp(c[1]))
            "relintersec" -> RelationIntersect(parseRelationExp(c[0]), parseRelationExp(c[1]))
            "relcomp" -> RelationComposition(parseRelationExp(c[0]), parseRelationExp(c[1]))
            "reltrans" -> RelationClosure(parseRelationExp(c[0]))
            "relinv" -> RelationDual(parseRelationExp(c[0]))
            "relident" -> ConceptIdeRelation(parseConceptExp(c[0]))

            else -> throw Exception("${e.nodeName} can't be converted to relation")
        }
    }

    private fun parsePropertyExp(e: Element): PropertyExp {
        return when(e.nodeName) {
            "propref" -> PropertyReference(e.getAttribute("name"))
            else -> throw Exception("${e.nodeName} can't be converted to property")
        }
    }

    private fun parseIndividualExp(e: Element): IndividualExp {
        return when(e.nodeName) {
            "indref" -> IndividualReference(e.getAttribute("name"))
            else -> throw Exception("${e.nodeName} can't be converted to individual")
        }
    }


    private fun parseTypeByTypename(type:String):Type {
        return when(type.toUpperCase()) {
            "INT" -> Integer()
            "STRING" -> Str()
            "BIGINT" -> BigInt()
            "BOOL" -> Bool()
            "DATE" -> Date()
            "GRADE" -> Grade()
            "SEMESTER" -> Semester()
            else -> throw Exception("$type can't be converted")
        }
    }


    private fun parseType(e: Element): Type {
        return when(e.nodeName) {
            "basetype" -> parseTypeByTypename(e.getAttribute("name"))
            else -> throw Exception("${e.nodeName} can't be converted to type")
        }
    }

    private fun parseValue(e: Element): Value {
        return when(e.nodeName) {
            "basevalue" ->

                when(e.getAttribute("typename").toUpperCase()){
                    "INT" -> IntValue(e.getAttribute("value").toInt())
                    "STRING" -> StringValue(e.getAttribute("value"))
                    "BIGINT" -> BigIntValue(BigInteger(e.getAttribute("value")))
                    "BOOL" -> BoolValue(true)
                    "DATE" -> DateValue(e.getAttribute("value"))
                    "GRADE" -> GradeValue(BigDecimal(e.getAttribute("value")).setScale(2))

                    "SEMESTER" -> {
                        val str = e.getAttribute("value")
                        val year = str.substring(0,2).toInt()
                        val isWinter = str[2] == '/'
                        SemesterValue(year,isWinter)
                    }

                    else -> throw Exception("${e.nodeName} can't be converted to basevalue")
                }
            else -> throw Exception("${e.nodeName} can't be converted to value")
        }
    }
}



fun main(args: Array<String>) {


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

    println(TypeChecker.checkVoc(v))

    val s = v.serializeToXml()
    println(s)

    val vocabulary = IdtParser.parse(s)

    println(TypeChecker.checkVoc(vocabulary))


}


fun getChildElement(e: Element): List<Element> {
    val children = mutableListOf<Element>()
    for (i in 0 until e.childNodes.length) {
        val n = e.childNodes.item(i)
        if (n.nodeType == Node.ELEMENT_NODE)
            children.add(n as Element)
    }
    return children
}

fun <T> getProperty(instance: Any, name: String): T {
    val clazz = instance.javaClass.kotlin
    clazz.declaredMemberProperties.forEach {
        if (it.name == name)
            return it.get(instance) as T
    }
    throw Exception("Property $name can't be found")
}



