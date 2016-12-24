from mysql.connector import MySQLConnection, Error
from read_config import read_db_config
import serial

# This functions inserts the data into the database
def insert_data(info_row):
    query = "INSERT into scdc_m3(glu, temp, hrt, bp) VALUES(%(glu)s, %(temp)s, %(hrt)s, %(bp)s)"
    try:
        db_config = read_db_config()
        conn = MySQLConnection(**db_config)

        cursor = conn.cursor()
        cursor.execute(query, info_row)

        if cursor.lastrowid:
            print('last insert id', cursor.lastrowid)
        else:
            print('last insert id not found')

        conn.commit()
    except Error as error:
        print(error)

    finally:
        cursor.close()
        conn.close()

# This function continuously reads the data
def read_data():
    count = 0
    l = []
    while True:
        if count == 0:
            cha = raw_input("Enter s to begin accepting data: ")
            if cha == 'e':
                return
            if cha != 's':
                print "Not correct character"
            else:
                ser.write('s')
            count+=1
        line = ser.readline().strip()
        print line
        l.append(float(line))
        count+=1
        if count == 5:
            print "Set of data received"
            info = {}
            info['glu'] = l[0]
            info['temp'] = l[1]
            info['hrt'] = l[2]
            info['bp'] = l[3]
            insert_data(info)
            l = []
            count = 0

def main():
    try:
        read_data()
    except Exception as e:
        print e

if __name__ == '__main__':
    ser = serial.Serial('/dev/ttyACM0', 9600)
    print "Input e to exit the program"
    main()
    ser.close()
