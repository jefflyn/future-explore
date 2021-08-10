create table open_gap
(
    trade_date  varchar(10)                          not null comment 'trade date',
    code        varchar(8)                           not null,
    name        varchar(10)                          not null comment 'name',
    category    varchar(8) default ''                not null comment 'category',
    pre_close   decimal(10, 2)                       not null comment 'pre close',
    open        decimal(10, 2)                       not null comment 'current open',
    gap_rate    decimal(10, 2)                       not null comment 'gap rate',
    remark      varchar(64) null comment 'remark',
    buy_low     decimal(10, 2)                       not null comment 'low price for buy',
    sell_high   decimal(10, 2)                       not null comment 'high price for sell',
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    constraint uidx_open_gap_code_trade_date
        unique (code, trade_date)
) comment 'open gap log';

