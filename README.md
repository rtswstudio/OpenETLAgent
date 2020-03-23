### OpenETL Agent

OpenETL is a platform to professionally manage your ETL (Extract, Transform, Load) process. The platform consists of an **Agent** as well as a **Portal**. This site focuses on the Agent, which is open-source and completely free to use. More information on the Portal can be found <a href="https://openetl-portal.appspot.com/">here</a>.

Key features of the Agent includes:

* Built-in set of **extract** plugins to read data from the source system
* Built-in set of **transform** plugins to manipulate the data
* Built-in set of **format** plugins to prepare the data for the destination
* Built-in set of **load** plugins to write the data to the destination system
* Pluggable design (develop your own plugins using a simple API if not built in)
* Light-weight and easy to deploy (just a single JAR file)
* Pluggable interface for metrics

## Overview

![Missing image...](doc/OpenETL_Architecture.png "OpenETL Overview")

### Extract

The **Extract** phase involves reading data from source systems. Built-in plugins include support for:

* Relational databases using JDBC (for example Oracle, SQL Server, MySQL, PostgreSQL)
* CSV files
* Microsoft Excel files
* HTML tables from URLs
* Microsoft Access databases

### Transform

The **Transform** phase involves manipulating the data. Built-in plugins include support for:

* Formatting date values
* Formatting numeric values
* Dropping columns
* Masking values (for example hashing)
* Renaming columns
* Trimming string values

### Format

The **Format** phase is a separation from the traditional ETL model, but very much necessary. Before loading the data, we need to define the output format. Built-in plugins include support for:

* CSV
* JSON
* JDBC (SQL statements)

### Load

The **Load** phase involves writing the data into a destination system. Built-in plugins include support for:

* Console output
* Local disk
* Relational databases using JDBC
* Microsoft Azure (Blob Storage)
* Google Cloud Services Storage

### Summary

The **summary** phase is intended to save metrics (for example number of tables, columns and rows processed, as well as any warnings or errors) concerning the run. Built-in plugins include support for:

* Console output
* Local disk
* HTTP (POST)
* OpenETL Portal
* Prometheus (Push Gateway)

## Documentation

For the full documentation, please see the <a href="https://github.com/rtswstudio/OpenETLAgent/wiki">OpenETL Agent Wiki</a>.


