INSERT INTO `portaldb`.`users` (`userid`, `name`, `pass`, `affiliation`, `role`) VALUES  "smarri4@uic.edu","Sai Sravith Reddy Marri","testpass","CRI", "admin");
INSERT INTO `portaldb`.`users` (`userid`, `name`, `pass`, `affiliation`, `role`) VALUES ("George Chlipala","gchlip2@uic.edu","testpass","CRI", "admin");
INSERT INTO `portaldb`.`users` (`userid`, `name`, `pass`, `affiliation`, `role`) VALUES ("sravith@uic.edu","Sai Marri","testpass","CRI", "customer");

insert into project_membership values("cridv-j7d0g-000000000000000","smarri4@uic.edu",1);
insert into project_membership values("cridv-j7d0g-000000000000000","sai@uic.edu",1);
insert into project_membership values("cridv-j7d0g-000000000000001","sai@uic.edu",0);
insert into project_membership values("cridv-j7d0g-000000000000002","sai@uic.edu",1);
insert into project_membership values("cridv-j7d0g-vw98lqg4dtjdf0f","sai@uic.edu",1);
insert into project_membership values("cridv-j7d0g-vw98lqg4dtjdf0f","abc@uic.edu",0);
insert into project_membership values("cridv-j7d0g-vw98lqg4dtjdf0f","xyz@uic.edu",0);

insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc', 1, 'raw', now(), 'smarri4@uic.edu', 'sample release1');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc1', 2, 'raw', now(), 'smarri4@uic.edu', 'sample release8');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc2', 3, 'raw', now(), 'smarri4@uic.edu', 'sample release2');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc3', 4, 'raw', now(), 'smarri4@uic.edu', 'sample release3');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc4', 5, 'result', now(), 'smarri4@uic.edu', 'sample release4');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc5', 6, 'result', now(), 'smarri4@uic.edu', 'sample release5');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc6', 7, 'report', now(), 'smarri4@uic.edu', 'sample release6');
insert into portaldb.project_files values('cridv-j7d0g-vw98lqg4dtjdf0f','abc7', 8, 'report', now(), 'smarri4@uic.edu', 'sample release7');
