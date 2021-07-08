# progetto-swd

The goal of this project is to perform an historical analysis of the test-suites related to components affected by architectural
smells in open-source systems, with the purpose of assessing whether the quality of these test suites decreases when architectural
smells are introduced. Nowadays project can contain several type of smells, in particular we focused on architectural and test smells,
that can negatively impact on different software qualities. Several Architectural and test smells have been considered for our purpose,
in particular for the Architectural we considered 18 type of smells: 6 at package level and the other 12 at class level. For test smells we
considered 7 type of smells. We have analyzed a list of projects selected on GitHub by specific criteria, in order to detect all the chosen
Architectural and test smells. We collected all these data about smells with the goal to detect if an increase of architectural smells leads
to an increase of test smells and in particular the presence of which architectural smell lead to which test smell. To compute this, we
used Association Rule method, showing that there are test smells like Assertion Roulette and Eager Test that more than other test
smells can be dependent by the presence of an architectural smell.
