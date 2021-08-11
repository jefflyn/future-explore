create table future_basic
(
    symbol      varchar(4)        not null comment '商品代号'
        primary key,
    name        varchar(16)       not null comment '商品名',
    type        varchar(8)        not null comment '分类',
    code        varchar(8)        not null comment '合约代码',
    low         decimal(10, 2)    not null comment '主连历史最低',
    high        decimal(10, 2)    not null comment '主连历史最高',
    amount      int               not null comment '合同每单位数量',
    unit        varchar(4)        not null comment '单位',
    `limit`     tinyint           not null comment '涨跌停限制%',
    margin_rate tinyint           not null comment '保证金率%',
    fee_type    tinyint           not null comment '手续费类型（1=金额，2=数量）',
    fee         decimal(8, 6)     null comment '每手开平手续费',
    fee_today   decimal(8, 6)     not null comment '平今手续费',
    night       tinyint           not null comment '是否夜盘（0=否 1=是）',
    exchange    varchar(16)       not null comment '所属交易所',
    is_target   tinyint default 1 not null comment '0=否 1=是',
    deleted     tinyint default 0 not null,
    remark      varchar(64)       null comment 'remark',
    update_time timestamp         not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '合约基本信息';

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

create table future_daily_high_low_log
(
    code       varchar(6)     null,
    trade_date date           not null,
    high       decimal(10, 2) null,
    high_time  time           null,
    low        decimal(10, 2) null,
    low_time   time           null
);

create index idx_future_daily_high_low_log_code
    on future_daily_high_low_log (code);

create table future_live
(
    code           varchar(8)     not null comment '合约code'
        primary key,
    name           varchar(16)    not null comment '合约名',
    price          decimal(10, 2) not null comment '现价',
    `change`       decimal(10, 2) not null comment '涨跌幅%',
    position       decimal        null comment '相对位置%',
    bid1           decimal(10, 2) null comment 'buy 1',
    ask1           decimal(10, 2) null comment 'sell 1',
    open           decimal(10, 2) null comment '开',
    low            decimal(10, 2) null comment '最低',
    high           decimal(10, 2) null comment '最高',
    amp            decimal(10, 2) null comment '振幅%',
    lowest_change  decimal(10, 2) null comment '历史最低涨跌幅%',
    highest_change decimal(10, 2) null comment '历史最高涨跌幅%',
    wave           varchar(64)    null comment '波动',
    update_time    datetime       not null comment '最新更新时间'
)
    comment '实时';

create table future_log
(
    id         bigint auto_increment comment 'id'
        primary key,
    name       varchar(20)    not null comment '合约名称',
    type       varchar(10)    null comment '类型',
    content    varchar(64)    null comment '内容',
    price      decimal(10, 2) null comment '当前价格',
    pct_change decimal(10, 2) null comment '当前涨跌幅',
    position   int            null comment '相对位置',
    log_time   datetime       not null comment '时间',
    remark     varchar(32)    null comment '备注'
)
    comment '日志';

create table future_monitor_log
(
    id            bigint auto_increment comment 'id'
        primary key,
    sno           varchar(16)    not null comment 'serial no.',
    code          varchar(8)     not null comment 'code',
    name          varchar(20)    not null comment 'contract name',
    type          varchar(10)    not null comment 'log type',
    content       varchar(64)    not null comment 'log contents',
    price         decimal(10, 2) not null comment 'logged current price',
    pct_change    decimal(10, 2) not null comment 'logged percentage change',
    position      int            not null comment 'relative position',
    position_lvl  tinyint        not null comment '[0,33]=1,[33,66]=2,[66,100]=3',
    `option`      tinyint        not null comment '1=call 2=put',
    suggest_price decimal(10, 2) not null comment 'suggest price',
    log_time      datetime       not null comment 'log time',
    remark        varchar(32)    null comment 'remark contents',
    constraint uidx_future_monitor_log_code
        unique (code),
    constraint uidx_future_monitor_log_sno
        unique (sno)
)
    comment '监控日志';

create table open_gap
(
    trade_date  varchar(10)                          not null comment 'trade date',
    code        varchar(8)                           not null,
    name        varchar(10)                          not null comment 'name',
    category    varchar(8) default ''                not null comment 'category',
    pre_close   decimal(10, 2)                       not null comment 'pre close',
    open        decimal(10, 2)                       not null comment 'current open',
    gap_rate    decimal(10, 2)                       not null comment 'gap rate',
    remark      varchar(64)                          null comment 'remark',
    buy_low     decimal(10, 2)                       not null comment 'low price for buy',
    sell_high   decimal(10, 2)                       not null comment 'high price for sell',
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    constraint uidx_open_gap_code_trade_date
        unique (code, trade_date)
)
    comment 'open gap log';

