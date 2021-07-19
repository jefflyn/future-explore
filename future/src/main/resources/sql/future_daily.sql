
create table future_daily
(
    id            bigint auto_increment comment '主键id'
        primary key,
    symbol        varchar(6)     not null comment '商品代码',
    trade_date    varchar(10)    not null comment '交易日期',
    code          varchar(10)    null comment '合约代码',
    name          varchar(20)    not null comment '合约名称',
    close         decimal(10, 2) null comment '收盘价',
    close_change  decimal(10, 2) null comment '昨收盘价涨跌幅',
    settle        decimal(10, 2) null comment '结算价',
    settle_change decimal(10, 2) null comment '结算价涨跌幅',
    open          decimal(10, 2) null comment '开盘价',
    high          decimal(10, 2) null comment '最高价',
    low           decimal(10, 2) null comment '最低价',
    hl_diff       decimal(10, 2) null comment '高低价差',
    amplitude     decimal(10, 2) null comment '振幅',
    pre_close     decimal(10, 2) null comment '昨收盘价',
    pre_settle    decimal(10, 2) null comment '昨结算价',
    deal_vol      int            null comment '交易量',
    hold_vol      int            null comment '持仓量',
    exchange      varchar(10)    null comment '交易所',
    create_time   datetime       not null comment '创建时间',
    update_time   datetime       not null comment '更新时间',
    remark        varchar(64)    null,
    constraint uidx_future_daily_trade_date_contract_code
        unique (trade_date, code)
)
    comment '每日数据';

create index idx_future_daily_code
    on future_daily (symbol);

create index idx_future_daily_code_date
    on future_daily (code, trade_date);

