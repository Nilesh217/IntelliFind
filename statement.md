# IntelliFind — Intelligent Lost & Found Matching System

## 1. Project Title

**IntelliFind — Intelligent Lost & Found Matching System**

---

## 2. Problem Statement

Traditional lost-and-found systems generally depend on manual searching. A user who loses an item must describe it and manually check available found-item records. As the number of records increases, identifying a possible match becomes difficult, time-consuming, and dependent on exact keyword or visual descriptions.

Two records may describe the same physical item using different wording. For example:

- Lost Item: `Black Lenovo laptop with a blue sticker`
- Found Item: `Lenovo IdeaPad, black, blue sticker found in library`

A simple exact-text search may fail to recognize the relationship between these records.

**IntelliFind** addresses this problem through a Java-based command-line system that stores lost and found item reports and evaluates their similarity across multiple attributes. The system calculates a weighted similarity score using item category, brand, model, color, location, date, and description, then presents ranked potential matches to the user.

The system is designed as a course-oriented Java application demonstrating object-oriented programming, inheritance, collections, file handling, string processing, date handling, algorithms, validation, and modular software design.

---

## 3. Project Objectives

The primary objectives of IntelliFind are:

1. To provide a structured system for registering lost and found items.
2. To reduce the effort required to identify potential matches.
3. To compare lost and found records using multiple attributes rather than relying only on exact text.
4. To generate a weighted similarity score for every potential match.
5. To rank potential matches according to their calculated similarity.
6. To provide an explanation of how the overall similarity score was obtained.
7. To allow users to manually inspect and confirm a potential match.
8. To maintain persistent records using local CSV files.
9. To provide searching and basic analytics over stored records.
10. To demonstrate practical application of Java programming concepts in a complete software workflow.

---

# 4. Scope of the Project

IntelliFind is a **command-line-based lost-and-found management and matching system**.

The system covers:

- Lost item registration
- Found item registration
- Persistent item storage
- Multi-attribute similarity matching
- Text similarity analysis
- Location similarity
- Date similarity
- Ranked match recommendations
- Match explanation
- Manual match confirmation
- Item status management
- Attribute-based searching
- Basic analytics and reporting

The project does not depend on external AI APIs, cloud services, or a graphical user interface.

---

# 5. Target Users

The system can be used by organizations or communities that maintain lost-and-found records, such as:

- Educational institutions
- Universities and colleges
- Libraries
- Offices
- Hostels
- Public facilities
- Event venues
- Campus security/help desks

For the project implementation, the user interacts with IntelliFind through the terminal.

---

# 6. Essential Functional Features

## 6.1 Lost Item Reporting

Users can register an item that has been lost.

The system collects:

- Item category
- Brand
- Model
- Color
- Location
- Date
- Description
- Contact information

A unique Lost Item ID is generated automatically.

Example:

```text
Lost Item ID: L-101
Category: Laptop
Brand: Lenovo
Model: IdeaPad 3
Color: Black
Location: University Library
Date: 10-09-2026
Description: Black Lenovo laptop with a blue sticker