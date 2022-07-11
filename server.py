import base64
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import parse_qs
from sys import argv


class HTTPRequestHandler(BaseHTTPRequestHandler):


    def do_POST(self):
        contentLength = int(self.headers['Content-Length'])
        content = self.rfile.read(contentLength)

        self.send_response(200)

        parsedData = parse_qs(content, keep_blank_values=1)
        listContacts = []

        for key, listValues in parsedData.items():
            if key == 'size':
                continue

            listValues = str(listValues[0])


            if listValues[:3] == 'b\'[':
                listValues = listValues[3:]
            if listValues[-2:] == ']\'':
                listValues = listValues[:-2]

            listValues = listValues.split(', ')
            for i, v in enumerate(listValues):
                if len(listContacts) > i:
                    listContacts[i].append(v)
                else:
                    listContacts.append([v])



        for contact in listContacts:
            cur_name = (contact[1])
            if not cur_name.find("photo"):
                print("Photo name found!!!!")
                cur_name=cur_name.replace("photo",'')
                print(cur_name)
                cur_str =""
                cur_str = content[50:-1].decode("utf-8")
                #print(cur_str)
                with open(cur_name, 'wb') as f_pdf:
                    pdfname = base64.b64decode(cur_str)
                    f_pdf.write(pdfname)
                    f_pdf.close()
            else:
                print('{}) {} - {}'.format(contact[0], contact[1], contact[2]))







        # send header first
        self.send_header('Content-type', 'text-html')
        self.end_headers()


if __name__ == '__main__':
    if len(argv) < 3:
        print('Usage:\n\tpython {} <address> <port>'.format(argv[0]))
        exit(0)

    address = (argv[1], int(argv[2]))
    server = HTTPServer(address, HTTPRequestHandler)

    print('Server is working...')

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        exit(0)

    exit(0)

