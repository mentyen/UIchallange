## Demo Challange

#### Setup
1. Create a new public github repo
2. Pull down this project and check it into your new github repo.
3. Setup the project in your IDE.
4. In src/test/resources update the config.properties file platform for your OS.
5. From command line run mvn clean install -U -DskipTests
6. Make sure you can run the DemoTest and chrome launches.


#### Test Cases

1. TC_01 HappyPath
2. TC_02 ValidatingQuantityFunctionality(Adding negative order quantity-->Order should not be placed-->Negative total balance should not be showing in a popUp windows)
3. TC_03 ValidatingPaymentInformation (Do not choose any of the payments -->Order should not be placed)
4. TC_04 ValidatingPickUpInformation (Do not choose a required PickUp Information --> Order should not be placed-->PopUp windows should appier)

