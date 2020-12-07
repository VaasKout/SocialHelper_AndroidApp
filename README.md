# Client-Server app for SocialHelper project ver 1.0
   This application helps pregnant women to get a seat and disabled people to call social worker and move freely on Moscow subway.
    It consists of 3 clicents:
        1.Pregnant
        2.Disabled (wheelchair especially)
        3.Social worker
Every client registers with login and password to use it's account and gets verification from server.
    1.Pregnant client connects by bluetooth to Arduino terminal and sends signal to activate lights, so people around can see that.
    2.Disabled client define route from one station to another, selects time to get escort from social worker and sends order to the server.
    3.Social worker client gets orders from Disabled client sorted on server and accepts them.
    
Application is written with single activity using arcitecture components.
