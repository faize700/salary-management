import random

departments = ["HR", "Engineering", "Finance", "Marketing", "Sales"]
countries = ["India", "USA", "UK", "Brazil", "Germany"]

with open("src/main/resources/data.sql", "w") as f:
    for i in range(1, 10001):
        name = f"Employee{i}"
        dept = random.choice(departments)
        country = random.choice(countries)
        salary = random.randint(3000, 15000)
        f.write(f"INSERT INTO employee (name, department, country, salary) VALUES ('{name}', '{dept}', '{country}', {salary});\n")
