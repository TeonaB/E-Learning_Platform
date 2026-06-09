# Platformă de E-Learning 

---

## 1. Descrierea Proiectului
Aplicația este o platformă de cursuri online (similară cu Udemy/Coursera) care permite instructorilor să creeze și să gestioneze cursuri și lecții, iar studenților să exploreze catalogul, să se înscrie la cursuri și să ofere recenzii. 

---

## 2. Diagrama ERD (Entity-Relationship Diagram)
<img width="790" height="545" alt="image" src="https://github.com/user-attachments/assets/0923c261-2494-4de1-883b-fac450d72de4" />


---

## 3. Cerințe Funcționale 

### 🌐 Vizitator (Neautentificat)
* **Explorare Catalog:** Poate vizualiza lista completă de cursuri disponibile în platformă.
* **Filtrare:** Poate filtra cursurile în funcție de categorii.
* **Înregistrare:** Își poate crea un cont nou de student prin completarea unui formular.

### 🎓 Student Autentificat (Rol: `USER`)
* **Înrolare în Cursuri:** Se poate înrola la orice curs din platformă.
* **Acces la Conținut:** După înrolare, studentul deblochează și poate vizualiza lista completă de lecții asociate exclusiv acelui curs.
* **Gestiune Recenzii:**
    * **Create:** Poate lăsa o recenzie pentru un curs la care este înrolat (acordând o notă/rating și un comentariu text).
    * **Read:** Își poate vedea propriile recenzii.
    * **Update:** Poate edita textul sau nota unei recenzii lăsate anterior.
    * **Delete:** Poate șterge o recenzie dacă dorește să o elimine.

### ⚙️ Administrator (Rol: `ADMIN`)
* **Management Utilizatori:** Are control total asupra conturilor din platformă; poate crea, vizualiza, edita detaliile sau șterge utilizatori, inclusiv gestionarea și schimbarea rolurilor acestora.
* **Management Cursuri & Lecții:** 
    * Creează cursuri noi, le asociază categorii, le modifică prețul/detaliile sau le șterge.
    * Adaugă, modifică sau elimină lecții din cadrul oricărui curs.
* **Management Recenzii:** Poate vizualiza toate recenziile de pe platformă și are dreptul de a șterge recenzii.
