var io = require('socket.io').listen(3001);
var distance = require('gps-distance');
var mongodb = require('mongodb');

var MongoClient = mongodb.MongoClient;
var ObjectID = mongodb.ObjectID;


var MAX_RECEIVER = 100; 
var clients: ChatUser[] = [];
var msg_queue: string[] = [];
var MAX_MSG_QUEUE_LEN = 30;
var db;

io.sockets.on('connection', function (socket) {
    console.log('user connected:', socket.id);
    socket.on('disconnect', () => {
        var position = clients.indexOf(socket.id);
        if (~position) clients.splice(position, 1);
    });
    //socket.emit('news', { hello: 'world' });
    socket.on('send_area_msg', function (msg) {
        //console.log(data);
        //socket.broadcast.emit('get_msg', data);

        msg = JSON.parse(msg);
        msg.timestamp = new Date().getTime();

        msg_queue.push(msg);

        if (msg_queue.length > MAX_MSG_QUEUE_LEN) msg_queue.pop();

        var near_clients = clients.filter((val) => {
            return distance([val.gps, clients[socket.id].gps]) <= clients[socket.id].max_dist;
        });

        if (MAX_RECEIVER < near_clients.length) clients[socket.id].dec_max_dist();

        near_clients.forEach((val) => {
            msg = JSON.stringify(msg);
            io.sockets.socket(val.id).emit("get_msg", msg);
        });
    });

    socket.on('update_profile', (data) => {
        var profile = JSON.parse(data);
        if (profile.id != undefined) {
            var id = profile.id;
            delete profile.id;
            db.collection('test_insert').update({ _id: new ObjectID(id) }, { $set: profile }, { w: 1 }, function (err) {
                if (err) console.warn(err.message);
                else console.log('successfully updated profile.');
            });
        } else {
            db.collection('test_insert').insert(profile, function (err, docs) {                
                console.log('successfully created profile.');
                socket.emit('get_id', docs._id);
                clients[socket.id].id = docs._id;
            });
        }
    });
    

    socket.on('update_location', function (location) {
        
        location = JSON.parse(location);
        console.log('update_location', location);
        clients[socket.id] = new ChatUser(socket.id, location);
        var temp = JSON.stringify(msg_queue);
        socket.emit('get_msg_buffer', temp);
    });   

});

MongoClient.connect('mongodb://127.0.0.1:27017/test', function (err, db_local) {
    if (err) throw err;
    db = db_local;  
})



class ChatUser {
    public id: string;
    public gps: number[];
    public max_dist: number = 10;
    public display_name: string;

    constructor(id, gps){
        this.id = id;
        this.gps = gps;
    }

    public dec_max_dist() {
        this.max_dist = Math.ceil(this.max_dist * 0.7);
    }
}

class Message {
    public text: string;
    public gps: number[];
    public timestamp: number;

    constructor(text, gps, timestamp) {
        this.text = text;
        this.gps = gps;
        this.timestamp = timestamp;
    }
}