Feature: Given different items get proper total value for each

  As a consumer I would like to obtain the proper value for a set of items

  Scenario: Buying several units of item A gives proper discount
    When a consumer makes an API POST request "/purchase" with body from file "request.json"
    Then a valid data response "response.json"