grammar BOL;
o: d* # Ontology;
d:    'individual' ID   # IndividualDecl
    | 'concept' ID      # ConceptDecl
    | 'relation' ID     # RelationDecl
    | 'property' ID ':' t # PropertyDecl
    | 'axiom' f # Axiom
 ;

f:  c '≡' c # ConceptEquality
  | c '⊑' c # ConceptSub
  | i 'is-a' c # ConceptFormula
  | i r i # RelationFormula
  | i p v # PropertyFormula
 ;



i: ID # IndividualReference ;

c:   ID # ConceptRefernece
   | '⊤' # UniversalConcept
   | '⊥' # EmptyConcept
   | c '⊔' c # ConceptUnion
   | c '⊓' c # ConceptIntersect
   | '∀' r '.' c # UniversalRelativization
   | '∃' r '.' c # ExistentialRelativization
   | 'dom' r # Domain
   | 'rng' r # Range
   | 'dom' p # PropertyDomain
;

r:   ID # RelationReference
   | r '∪' r # RelationUnion
   | r '∩' r # RelationIntersect
   | r ';' r # RelationComposition
   | r '*' # RelationClosure
   | r '-1' # DualRelation
   | '∆' c # ConceptIdentityRelation
   ;

p: ID # PropertyReference;




t:   'int'              # Int
    | 'float'           # Float
    | 'bool'            # Bool
    | 'string'          # String
;

v:    Int     # IntValue
    | String  # StringValue

;


WS: [ \t\r\n]+ -> skip;
ID: [a-zA-Z][a-zA-Z0-9]*;

String: ["].*?["];
Int: [0-9]+;
