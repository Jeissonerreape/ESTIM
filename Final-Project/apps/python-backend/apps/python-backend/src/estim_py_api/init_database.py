import psycopg2
import os

def init_database():
    """Ejecuta los scripts SQL de migración"""
    conn = psycopg2.connect(
        dbname=os.getenv('DB_NAME'),
        user=os.getenv('DB_USER'),
        password=os.getenv('DB_PASS'),
        host=os.getenv('DB_HOST'),
        port=os.getenv('DB_PORT')
    )
    
    cursor = conn.cursor()
    
    try:
        # Ejecutar migraciones en orden
        migrations = [
            "migrations/0001_enable_extensions.sql",
            "migrations/0002_enum_types.sql", 
            "migrations/0003_tables_and_flc.sql",
            "migrations/0004_indexes_and_friggers.sql"
        ]
        
        for migration in migrations:
            if os.path.exists(migration):
                with open(migration, 'r') as file:
                    sql_script = file.read()
                    cursor.execute(sql_script)
                print(f"✅ Ejecutada: {migration}")
            else:
                print(f"⚠️ No encontrada: {migration}")
        
        conn.commit()
        print("🎉 Base de datos inicializada correctamente")
        
    except Exception as e:
        print(f"❌ Error inicializando BD: {e}")
        conn.rollback()
    
    finally:
        cursor.close()
        conn.close()
