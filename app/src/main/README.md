Solution to theoretical questions:
1. Request token from the server every time
    Pro: In this way we have the most up-to-date token - if the server invalidate some given token
         or perform an update we will get it;
    Con: Unnecessary traffic - creates many network requests;

2. Saving token locally:
    Pro: Less traffic, because we send less network requests;
    Con: We may use the old invalidated token when server perform an update or trying to
         invalidate a token;


I pledge the highest level of ethical principles in support of academic excellence.
I ensure that all of my work reflects my own abilities and not those of someone else.
Amirgamzaev Amir I.D.: 332494103