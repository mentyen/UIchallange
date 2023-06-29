#Ctrl+Alt+Shift+L
@uitest
Feature: UI TEST

  Scenario Outline: <scenario> Ensure user is able to launch the url
    Given User launch the app url
    And Verify user lands on a home page
    Then Validate home page attributes for "<userType>"
    Examples:
      | userType | scenario |
      | general    | S1       |
      | admin    | S2       |
      | admin    | S3       |
      | admin    | S4       |




