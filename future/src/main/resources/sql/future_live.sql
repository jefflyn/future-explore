
create table future_live
(
    code           varchar(8)     not null comment '合约code'
        primary key,
    name           varchar(16)    not null comment '合约名',
    price          decimal(10, 2) not null comment '现价',
    `change`       decimal(10, 2) not null comment '涨跌幅%',
    bid1           decimal(10, 2) null comment 'buy 1',
    ask1           decimal(10, 2) null comment 'sell 1',
    low            decimal(10, 2) null comment '最低',
    high           decimal(10, 2) null comment '最高',
    position       decimal(10, 2) null comment '相对位置%',
    amp            decimal(10, 2) null comment '振幅%',
    wave           varchar(64)    null comment '波动',
    lowest_change  decimal(10, 2) null comment '历史最低涨跌幅%',
    highest_change decimal(10, 2) null comment '历史最高涨跌幅%',
    update_time    datetime       not null comment '最新更新时间'
)
    comment '现场直播';

