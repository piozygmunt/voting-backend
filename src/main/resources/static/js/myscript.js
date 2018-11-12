let stompClient = null;


class MyComponent extends React.Component {
    render() {
        return (
            <h1 className="myclass">{this.props.text} </h1>
        )

    }
}

console.log("zaladowany");

ReactDOM.render(<MyComponent text="react dziala "/>, document.getElementById('napis'));


function connect(vm) {

    let socket = new SockJS('/voting-socket');
    stompClient = Stomp.over(socket);
    let headers = {};
    headers[headerName] = token;
    stompClient.connect(headers, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/voting/' + procId, function (data) {
            console.log("subscribed to " + procId);
            console.log("data: " + data.body);
            console.log("data12: " + data.body.error);
            console.log("current error: " + vm.error);
            let response = JSON.parse(data.body);
            vm.error(response.error);
            console.log("current error: " + vm.error);

        });
    });
}


function sendData(itemIndex) {
    stompClient.send("/app/action/" +procId, {}, JSON.stringify({'itemId': itemIndex }));
}

$(function () {
    function AppViewModel() {
        this.error = ko.observable("possib");
    }

    let vm = new AppViewModel();
// Activates knockout.js
    ko.applyBindings(vm);


   $("button").on('click', function (event) {
       event.preventDefault();
       var itemId = $(this).parent().data("item-id");
       sendData(itemId);
       console.log("clicked: " + itemId);
   })
    console.log("ASD");
   connect(vm);
});