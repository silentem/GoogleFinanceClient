update stock set watch = 2, active = 1 where barsnumber > 150 and lastprice > 10 and lastprice < 30 ;
update stock set watch = 0 where barsnumber < 1200 ;

SELECT * FROM stock where watch = 2 and beta < 3 order by symbol;

update stock set beta = 0;
update stock set beta = 0 where beta < 2 and watch = 2;
update stock set lowdeviation = 0;

update proxy set beentosorrylist = 0 where beentosorrylist is null;
update proxy set responsetime = 0, counter = 0;
update proxy set active = 0 where id < 126;
update proxy set beentosorrylist = beentosorrylist - 1 where beentosorrylist > 0;

SELECT * FROM proxy p;
SELECT * FROM proxy p where active = 1 order by 4 desc;
SELECT * FROM proxy p where beentosorrylist > 0 and active = 1 order by 7 desc;
SELECT * FROM proxy p where active = 0;
select * from proxy where ip = '69.163.202.55';
select * from proxy where url like '%proxyglype%';


SELECT * FROM stock s where active = 1 order by 2;
SELECT * FROM stock s where beta = 0;


SELECT * FROM bar b;
select count(*), symbol from bar group by symbol having count(*) < 700;
SELECT * FROM volume v;