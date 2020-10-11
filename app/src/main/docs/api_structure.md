# Description of used RESTful API

## https://documenter.getpostman.com/view/10808728/SzS8rjbc#27454960-ea1c-4b91-a0b6-0468bb4e6712
## Main entry point: https://api.covid19api.com/

| Purpose                                             | Request                                                                     |
| :-------------------------------------------------- | :-------------------------------------------------------------------------- |
| Get country list                                    | `GET /countries`                                                            |
| Get country's confirmed cases in given time period  | `GET /total/country/{country}/status/confirmed?from={dateFrom}&to={dateTo}` |
| Get all country's cases since started recording     | `GET total/country/{country}`                                               |
| Get all world's cases since started recording       | `GET /world/total`                                                          |