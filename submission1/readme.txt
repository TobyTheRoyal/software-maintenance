Group12 - Task 1

Tobias Hamedl       (11808141) tobias.hamedl@student.tugraz.at
Gerson Miguel Joao  (11804202) gerson.joao@student.tugraz.at
Mahmoud Khalil      (01651551) khalil@student.tugraz.at
Constantin Piber    (11921514) constantin.piber@student.tugraz.at


Implemented tasks:
------------------------
Generate Control Flow Graph
Generate Program Dependency Graph (Control & Data Dependency)
Calculate Slice from PDG


Remarks:
------------------------
CFG Nodes work with preceding nodes
CFG Nodes influencedBy control statement of outer block
CFG contains Start and End (statement=null)
PDG Nodes have controlDependencies (list) and influencedBy (parent control)
PDG Nodes have a list of nodes which it has a data dependecy on (other way
  compared to controlDependencies)
