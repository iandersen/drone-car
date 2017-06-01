from selenium import webdriver
from urllib2 import urlopen

url = 'http://localhost/mapTest.html'
file_name = './test.txt'

conn = urlopen(url)
data = conn.read()
conn.close()

file = open(file_name,'wt')
file.write(data)
file.close()

browser = webdriver.Firefox()
browser.get('file:///'+file_name)
html = browser.page_source
browser.quit()