# jpm #

### Steps of development and some arrived questions: ###
  1. extracted interfaces and classes from exercise
  2. defined interfaces and basic concept of communication
  3. defined concept for testing of solution by implementing stub of function Test
  4. implemented Gateway and Message from interfaces
  5. designed ResourceScheduler and implemented basic communication between ResourceScheduler and Test
  6. configured Java Message Service JMS at Glassfish server
  7. implemented and tested JMS callback interface between ResourceScheduler and Gateway for indicating of completed() messages
   Question: Use of Java Events could be in some cases better, but JMS is more universal through all apps in JEE server
  8. implemented Java Message Service between ResourceScheduler and Test
  9. testing of communication and implementation of semaphore for handling Gateway resource
  10. designed sorter interface and implemented sorting by group id arrival order
    Question: ambiguous definition of algorithm: When there were already groups processed, but now there are not any messages in Queue, should be order of new messages by previous group arrival or not?
  11. implemented and tested sorting algorithm with Qualifier for CDI selection of actual algorithm implementation
  12. implemented alternative algorithm for sorting by group id
  13. optimisation of EJB beans, injections and testing
    Question: What is standard in commenting of source codes?
    Question: What is standard in unit and function test coverage?
  14. implemented canceled and termination group properties
  15. refactoring of sources and additional commenting of work
  16. GitHub upload
  17. clean up
