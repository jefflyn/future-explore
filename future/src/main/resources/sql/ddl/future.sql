create table future_basic
(
    symbol varchar(4) not null comment '商品代号'
        primary key,
    name varchar(16) not null comment '商品名',
    type varchar(8) not null comment '分类',
    code varchar(8) not null comment '合约代码',
    low decimal(10,2) not null comment '主连历史最低',
    high decimal(10,2) not null comment '主连历史最高',
    a decimal(10,2) null comment '本合约A',
    b decimal(10,2) null comment '本合约B',
    c decimal(10,2) null comment '本合约C',
    d decimal(10,2) null comment '本合约D',
    amount int not null comment '合同每单位数量',
    unit varchar(4) not null comment '单位',
    night tinyint not null comment '是否夜盘（0=否 1=是）',
    exchange varchar(16) not null comment '所属交易所',
    is_target tinyint default 1 not null comment '0=否 1=是',
    relative varchar(128) null comment '关联code',
    deleted tinyint default 0 not null,
    remark varchar(64) null comment 'remark',
    update_time timestamp not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '合约基本信息';

create table future_collect
(
    id bigint auto_increment comment '主键id'
        primary key,
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) not null comment '合约代码',
    name varchar(20) not null comment '合约名称',
    type tinyint(1) not null comment '采集类型',
    price decimal(10,2) not null comment '现价',
    position tinyint default 0 not null comment '相对位置',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    remark varchar(64) null
)
    comment '定时数据采集分析';

create index idx_future_collect_trade_date
    on future_collect (trade_date);

create index idx_future_daily_collect_code_date
    on future_collect (code, trade_date);

create table future_daily
(
    id bigint auto_increment comment '主键id'
        primary key,
    symbol varchar(6) not null comment '商品代码',
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) null comment '合约代码',
    name varchar(20) not null comment '合约名称',
    close decimal(10,2) null comment '收盘价',
    close_change decimal(10,2) null comment '昨收盘价涨跌幅',
    settle decimal(10,2) null comment '结算价',
    settle_change decimal(10,2) null comment '结算价涨跌幅',
    open decimal(10,2) null comment '开盘价',
    high decimal(10,2) null comment '最高价',
    low decimal(10,2) null comment '最低价',
    hl_diff decimal(10,2) null comment '高低价差',
    amplitude decimal(10,2) null comment '振幅',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    exchange varchar(10) null comment '交易所',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    remark varchar(64) null,
    constraint uidx_future_daily_trade_date_contract_code
        unique (trade_date, code)
)
    comment '每日数据';

create index idx_future_daily_code
    on future_daily (symbol);

create index idx_future_daily_code_date
    on future_daily (code, trade_date);

create table future_live
(
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null comment '合约code'
        primary key,
    type varchar(8) not null comment '品种',
    name varchar(16) not null comment '合约名',
    price decimal(10,2) not null comment '现价',
    `change` decimal(10,2) not null comment '涨跌幅%',
    position tinyint default 0 not null comment '相对位置%',
    bid1 decimal(10,2) null comment 'buy 1',
    ask1 decimal(10,2) null comment 'sell 1',
    open decimal(10,2) null comment '开',
    low decimal(10,2) null comment '最低',
    high decimal(10,2) null comment '最高',
    amp decimal(10,2) null comment '振幅%',
    lowest_change decimal(10,2) null comment '历史最低涨跌幅%',
    highest_change decimal(10,2) null comment '历史最高涨跌幅%',
    wave varchar(64) null comment '波动',
    update_time time not null comment '最新更新时间'
)
    comment '实时';

create index idx_future_live_trade_date_code
    on future_live (trade_date, code);

create table future_log
(
    id bigint auto_increment comment 'id'
        primary key,
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null comment 'contract code',
    name varchar(10) not null comment '合约名称',
    type varchar(10) null comment '类型',
    factor int null comment '监控因子',
    diff decimal(10,2) null comment '监控值',
    content varchar(64) null comment '内容',
    `option` varchar(6) not null comment '操作选择',
    suggest decimal(10,2) not null comment '建议价格',
    pct_change decimal(10,2) null comment '当前涨跌幅',
    position int null comment '相对位置',
    log_time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    remark varchar(32) null comment '备注'
)
    comment '日志';

create index idx_future_log_code_date
    on future_log (code, trade_date);

create index idx_future_log_trade_date
    on future_log (trade_date);

create index uidx_future_log
    on future_log (code, type, factor, suggest, position);

create table future_wave
(
    code text null,
    start text null,
    end text null,
    a double null,
    b double null,
    c double null,
    d double null,
    ap double null,
    bp double null,
    cp double null,
    dp double null
);

create table future_wave_detail
(
    code text null,
    begin text null,
    end text null,
    status text null,
    begin_price double null,
    end_price double null,
    `change` double null,
    days bigint null
);

create table future_week_stat
(
    code varchar(16) not null,
    week varchar(10) not null,
    `change` decimal(10,1) null,
    pct_change decimal(10,2) null,
    constraint future_week_stat_pk
        unique (code, week)
);

create table gap_log
(
    code varchar(16) null comment 'code',
    start_date varchar(10) null comment '产生缺口日期',
    end_date varchar(10) not null comment '缺口结束日期',
    gap_type varchar(10) not null comment '类型',
    start_price decimal(10,2) not null comment '缺口开始价格',
    end_price decimal(10,2) not null comment '缺口结束价格',
    gap_rate decimal(10,2) not null comment '缺口大小%',
    is_fill tinyint default 0 not null comment '是否已回补（0=未回补，1=已回补）',
    fill_date varchar(10) null comment '回补日期',
    create_time datetime null comment '插入时间',
    update_time datetime null comment '更新时间',
    constraint idx_gap_log_code_date
        unique (code, start_date)
)
    comment '缺口记录';

create table open_gap
(
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null,
    name varchar(10) not null comment 'name',
    category varchar(8) default '' not null comment 'category',
    pre_close decimal(10,2) not null comment 'pre close',
    pre_high decimal(10,2) null comment 'pre high',
    pre_low decimal(10,2) null comment 'pre low',
    open decimal(10,2) not null comment 'current open',
    gap_rate decimal(10,2) not null comment 'gap rate',
    day_gap tinyint null comment 'is day gap',
    remark varchar(64) null comment 'remark',
    buy_low decimal(10,2) not null comment 'low price for buy',
    sell_high decimal(10,2) not null comment 'high price for sell',
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint uidx_open_gap_code_trade_date
        unique (code, trade_date)
)
    comment 'open gap log';

create index idx_open_gap_trade_date
    on open_gap (trade_date);

create table stock_daily
(
    ts_code text null,
    trade_date text null,
    close double null,
    open double null,
    high double null,
    low double null,
    pre_close double null,
    pct_change double null,
    vol bigint null,
    amount double null,
    vwap double null
);

create table ts_future_contract
(
    ts_code varchar(16) not null comment '合约代码'
        primary key,
    symbol varchar(8) not null comment '交易标识',
    exchange varchar(8) not null comment '交易所代码 CFFEX-中金所 DCE-大商所 CZCE-郑商所 SHFE-上期所 INE-上海国际能源交易中心',
    name varchar(16) null comment '中文简称',
    fut_code varchar(8) null comment '合约产品代码',
    type tinyint not null comment '合约类型（1=连续 2=主力 3=常规）',
    related_code varchar(10) null comment '主力关联合约'
)
    comment '合约列表';

create table ts_future_daily
(
    ts_code varchar(16) not null comment 'TS合约代码',
    trade_date varchar(10) not null comment '交易日期',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    open decimal(10,2) not null comment '开盘价',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    close decimal(10,2) not null comment '收盘价',
    settle decimal(10,2) not null comment '结算价',
    close_change decimal(10,2) not null comment '昨收盘价涨跌幅',
    settle_change decimal(10,2) not null comment '结算价涨跌幅',
    deal_vol decimal(10,1) null comment '成交量(手)',
    deal_amount decimal(10,2) null comment '成交金额(万元)',
    hold_vol decimal(10,1) null comment '持仓量(手)',
    hold_change decimal(10,1) null comment '持仓量变化',
    create_time datetime not null comment '创建时间',
    constraint idx_ts_future_daily_code_date
        unique (ts_code, trade_date)
)
    comment '日线数据';

create table ts_future_holding
(
    trade_date varchar(10) not null comment '日期',
    symbol varchar(10) not null comment '合约代码',
    broker varchar(10) not null comment '会员简称',
    vol int default 0 not null comment '成交量',
    vol_chg int default 0 not null comment '成交量变化',
    long_hld int default 0 not null comment '持买仓量',
    long_chg int default 0 not null comment '持买仓量变化',
    short_hld int default 0 not null comment '持卖仓量',
    short_chg int default 0 not null comment '持卖仓量变化',
    constraint idx_ts_future_holding_trade_date
        unique (trade_date, symbol, broker)
)
    comment '持仓变化';

create definer = root@localhost function STR_SPLIT(s text, del char, i int) returns varchar(32) deterministic security invoker
-- missing source code
;

