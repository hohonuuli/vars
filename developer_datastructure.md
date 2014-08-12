---
layout: default
title: VARS Database Schema
---

# Database Schema

The VARS data model consists of 2 distinct models, the annotation model and the knowledgebase model. These are represented in the Java applications as UML diagrams and in the Database as ER diagrams. The mapping between Java classes and the database tables is straightforward. A Java object typically represents a row in the database table of the same name (i.e. an Observation object is stored in the Observation table). 

The annotaiton and knowldegebase databases can be hosted in different databases or even on different servers. At MBARI, we have several different annotation databases for different purposes. However, we only a single knowledgebase database that is shared by all other VARS applications.

## Annotation Schema

### UML Diagram

<p align="center">
    <a href="images/developer_AnnotationClasses.gif"><img width="600" src="images/developer_AnnotationClasses.gif" /></a>
</p>

### ER Diagram

<p align="center">
    <a href="images/developer_AnnotationER.gif"><img width="600" src="images/developer_AnnotationER.gif" /></a>
</p>

## Knowledgebase Schema

<p align="center">
    <a href="images/developer_KnowledgebaseClasses.gif"><img width="600" src="images/developer_KnowledgebaseClasses.gif" /></a>
</p>

### ER Diagram

<p align="center">
    <a href="images/developer_KnowledgebaseER.gif"><img width="600" src="images/developer_KnowledgebaseER.gif" /></a>
</p>


