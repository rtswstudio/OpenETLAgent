### OpenETL Agent

OpenETL is a platform to professionally manage your ETL (Extract, Transform, Load) process. The platform consists of an **Agent** as well as a **Portal**. Key features include:

* Built-in set of common extract plugins (e.g. JDBC, CSV)
* Built-in set of common transform plugins (e.g. Mask Column, Rename Column, Drop Column)
* Built-in set of common format plugins (e.g. CSV, JSON)
* Built-in set of common load plugins (e.g. Disk, Console)
* Pluggable design (develop your own plugins using a simple API)
* Light-weight and easy to deploy (just a single JAR file)
* Pluggable interface for metrics
* Pluggable interface for exception handling

## Overview

![Missing image...](doc/OpenETL_Architecture.png "OpenETL Overview")

### Extract

The **Extract** phase involves reading data from various source systems, for example:

* Relational databases using JDBC (for example Oracle, SQL Server, MySQL, PostgreSQL)
* Local files (CSV)

### Transform

The **Transform** phase involves manipulating the incoming data, for example:

* Modifying column names (dropping, renaming)
* Modifying column values (anonymizing)
* Formatting dates and numbers

### Format

The **Format** phase is a separation from the traditional ETL model, but very much necessary. Before loading the data, we need to define the output format, for example:

* Comma separated values (CSV)
* JSON

### Load

The **Load** phase involves writing the data into a destination system, for example:

* Console output
* Local disk

## Connectors

## Usage

The OpenETL Agent can be run from the command line or programmatically from your Java application. Typically, if running from the command line, multiple instances are started from the Windows Task Scheduler or Unix/Linux CRON Scheduler.

*Please note the use of multiple transforms in the examples below.*

### Command Line

**Windows**

```
java -cp openetl-0.1.0.jar com.rtsw.openetl.agent.Agent
    -e extract.properties
    -t transform1.properties
    -t transform2.properties
    -t transform3.properties
    -f format.properties
    -l load.properties
    -s summary.properties
```

**Unix/Linux**

```
java -cp openetl-0.1.0.jar com.rtsw.openetl.agent.Agent
    -e extract.properties
    -t transform1.properties
    -t transform2.properties
    -t transform3.properties
    -f format.properties
    -l load.properties
    -s summary.properties
```

### Programmatically (Java)

```
import com.rtsw.openetl.agent.Agent;

Agent agent = new Agent.Builder()
        .extract("extract.properties")
        .transform("transform1.properties")
        .transform("transform2.properties")
        .transform("transform3.properties")
        .format("format.properties")
        .load("load.properties")
        .summary("summary.properties")
        .build();

agent.run();
```

## Development

## Configuration Reference

### Extract

#### CSVExtractConnector

Extracts table, column and row information from CSV files.

**Notes**

* The original filename is used as the table name
* The first line of the file can be used to define column names

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
source | Source folder for input files | Yes | *None* | /tmp
filename_pattern | Include matching filenames (REGEXP) | Yes | *None* | *.csv
header | Use first line as header (column names) | No | true | *None*
recursive | Scan any possible sub-directories | No | false | *None*
separator | Separate values using this string | No | ; | *None*
drop_extension | Drop file extension from table name | No | true | *None*
infer_schema | Try to detect column type from values | No | true | *None*
infer_schema_rows | How many rows to use in detecting column type | No | 1000 | 1000
infer_schema_date_format | Date format to use in detecting date type | No | dd.MM.yyyy | *None*

infer_schema_date_format: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html

**Report metrics**

* Tables: Number of tables extracted (1 file = 1 table)
* Columns: Number or columns extracted from all tables
* Rows: Number of rows extracted from all tables

*Since 0.1.0*

#### JDBCExtractConnector

Extracts table, column and row information from databases supporting Java Database Connectivity (JDBC).

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
class_name | The JDBC driver class name | Yes | *None* | com.mysql.jdbc.Driver
url | The JDBC url | Yes | *None* | jdbc:mysql://localhost:3306/default
username | Authenticate to source database using this username | No | *None* | *None*
password | Authenticate to source database using this password | No | *None* | *None*
table_pattern | Include matching table names (REGEXP) | No | *None* | *None*
batch_size | Return this many rows at a time for a single query | No | 10000 | *None*

**Report metrics**

* Tables: Number of tables extracted
* Columns: Number or columns extracted from all tables
* Rows: Number of rows extracted from all tables

*Since 0.1.0*

### Transform

#### DateFormatTransform

Formats date values to the specified string representation.

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
format | Use this format to represent date values | Yes | *None* | dd.MM.yyyy HH:mm:ss
table_pattern | Apply to matching table names (REGEXP) | Yes | *None* | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None* | *None*

format: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: Number of rows transformed from all tables

*Since 0.1.0*

#### DecimalFormatTransform

Formats floating point number values to the specified string representation.

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
format | Use this format to represent decimal values | Yes | *None* | #.####
table_pattern | Apply to matching table names (REGEXP) | Yes | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None*

format: https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: Number of rows transformed from all tables

*Since 0.1.0*

#### DropColumnTransform

Drops columns from tables.

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
table_pattern | Apply to matching table names (REGEXP) | Yes | *None* | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None* | *None*

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: N/A

*Since 0.1.0*

#### MaskColumnTransform

Masks values according to the specified mask policy.

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
table_pattern | Apply to matching table names (REGEXP) | Yes | *None* | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None* | *None*
mask_policy | Mask values according to this policy | Yes | *None* | empty, hide

**Mask policy:**

* empty: Replace values with empty strings ("")
* hide: Replace values with asterisks (*)
* md5: Calculate a one-way MD5 checksum from the values (performance may be impacted on larger data sets)

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: Number of rows transformed from all tables

*Since 0.1.0*

#### NoTransform

Leaves tables and rows unaffected (for convenience where it is easier to replace properties than removing configurations).

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: Number of rows transformed from all tables

*Since 0.1.0*

#### RenameColumnTransform

Renames columns from tables.

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
table_pattern | Apply to matching table names (REGEXP) | Yes | *None* | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None* | *None*
new_name | Rename matching columns to this | Yes | *None* | *None*

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: N/A

*Since 0.1.0*

#### TrimColumnTransform

Trims string values (removes empty spaces from the beginning and end).

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
table_pattern | Apply to matching table names (REGEXP) | Yes | *None* | *None*
column_pattern | Apply to matching column names (REGEXP) | Yes | *None* | *None*
trim_name | Trim column name | No | true | *None*
trim_value | Trim column value (string type) | No | true | *None*

**Report metrics**

* Tables: Number of tables affected
* Columns: Number of columns affected from all tables
* Rows: Number of rows transformed from all tables

*Since 0.1.0*

### Format

#### CSVFormat

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
column_separator | Separate columns using this string | No | ; | *None*
line_separator | Separate lines using this string | No | \n | *None*
header | Include column names as first line | No | true | *None*
encoding | Use this character encoding | No | UTF-8 | *None*

**Report metrics**

* Tables: Number of tables processed
* Columns: Number of columns processed from all tables
* Rows: Number of rows processed from all tables

*Since 0.1.0*

#### JDBCFormat

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---

**Report metrics**

* Tables: Number of tables processed
* Columns: Number of columns processed from all tables
* Rows: Number of rows processed from all tables

*Since 0.1.0*

#### JSONFormat

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
pretty | Pretty-print output | No | false | *None*
encoding | Use this character encoding | No | UTF-8 | *None*

**Report metrics**

* Tables: Number of tables processed
* Columns: Number of columns processed from all tables
* Rows: Number of rows processed from all tables

*Since 0.1.0*

### Load

#### ConsoleLoadConnector

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---

**Report metrics**

* Tables: Number of tables loaded
* Columns: Number of columns loaded from all tables
* Rows: Number of rows loaded from all tables

*Since 0.1.0*

#### DiskLoadConnector

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
destination | Destination folder for output files | Yes | *None*
compress | Compress output files using GZIP | No | false

**Report metrics**

* Tables: Number of tables loaded
* Columns: Number of columns loaded from all tables
* Rows: Number of rows loaded from all tables

*Since 0.1.0*

#### JDBCLoadConnector

Property | Description | Required | Default value | Example value
--- | --- | --- | --- | ---
class_name | The JDBC driver class name | Yes | *None* | com.mysql.jdbc.Driver
url | The JDBC url | Yes | *None* | jdbc:mysql://localhost:3306/default
username | Authenticate to destination database using this username | No | *None* | *None*
password | Authenticate to destination database using this password | No | *None* | *None*

**Report metrics**

* Tables: Number of tables loaded
* Columns: Number of columns loaded from all tables
* Rows: Number of rows loaded from all tables

*Since 0.1.0*
