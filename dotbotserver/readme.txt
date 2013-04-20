api: 

get URL/io/list <- get everything in the DB
get URL/io <- pull update on the queue
push URL/io <- push update from the queue
get URL/io/reset?id=n&from=master <- reset the slave stream of a particular id, only master can call this.
get URL/io/resetall?from=master <- reset all slave streams
get URL/io/alive <- get list of which platforms are alive/dead
post URL/io/alive?from=master <- inform server that platform is alive


Explanation of API calls:

Send to server:

Both Slave & Master:
Required: 
id: int (slave_id)
from: str (master | slave)
time: Unix Time in ms

IF from master:
state: str (go | pause | halt | sleep | wake)
EITHER:
(opt) def: array // functions are defined here
(opt) main: array // main loop is here
(opt) background: array // background loop is here
OR:
(opt) cmd: object // code to execute is defined here
OR:
None of the above

IF from slave:
status: arr // list of updates the slave wants to send back to the master
(opt) err: str // capture error messages
(opt) msg: str // send logs / STDOUT bcak

reply from server:
status: int // HTTP code (e.g. 200, 401, etc)
int: int slave_id
from: str (master | slave)
time_received: int
time_sent: int

optional:
(opt) err: str
(opt) msg: str

++ can forward everything from the master/slave



