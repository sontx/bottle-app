# bottle-app
Anonymous social network. Users can share their messages in rooms or put it in map and then other can chat with owner of that messages. Noone knows each other and noone knows you except for me :))
Everything will be deleted after a period of time such as messages(over 24H since created), chat conversations(over 24H since lastest message sent).

# Demo
<a href="https://youtu.be/D_r3XtSojUQ">
<img src="https://1.bp.blogspot.com/-BaPO3vZnpV4/WTPPxPsC59I/AAAAAAAAQnY/K0g7Z_AEeqkLWx_tU9KBxUdZcv0TCGrHQCLcB/s1600/Capture.PNG"/>
</a>

# Current features
Everything will be updated in realtime such as room messages, markers in map, chat messages, conversations...

<img src="https://2.bp.blogspot.com/-XRsbowiPSjY/WTPTYjwg3sI/AAAAAAAAQng/Do9AMKoTwksB8kIKNL2kQNnz-NsmyRaPQCLcB/s1600/Capture.PNG"/>

# Architecture
1. Firebase database: Realtime chat and manage users.
2. <a href="https://github.com/sontx/bottle-server">Main server</a>: Manage messages, users setting, delete messages and conversations that were expired.
3. <a href="https://github.com/sontx/bottle-fs">Upload server</a>: Manage storing files.
