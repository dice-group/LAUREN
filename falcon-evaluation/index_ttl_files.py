from elasticsearch import Elasticsearch, helpers
import json
import time

def read_turtle_file(filename):
    with open(filename, 'r') as f:
        data = f.readlines()

    dict_data = {}
    for num, line in enumerate(data):
        if num == 0: #skip the fist line
            continue
        tokens = line.split()
        uri = tokens[0].replace('<', '').replace('>', '')
        label = tokens[2]
        dict_data[uri] = label
    return dict_data


if __name__ == '__main__':
    start_time = time.time()
    uri_label_dict = read_turtle_file('data/data/labels_en.ttl')
    # variable for the Elasticsearch document _id
    id_num = 0

    # empty list for the Elasticsearch documents
    doc_list = []
    for uri, label in uri_label_dict.items():
        # dict object for the document's _source data
        doc_source = {"uri": uri, "label": label}
        id_num += 1
        # Elasticsearch document structure as a Python dict
        doc = {
            # pass the integer value for _id to outer dict
            "_id": id_num,

            # nest the _source data inside the outer dict object
            "_source": doc_source,
        }
        doc_list += [doc_source]

    # declare a client instance of the Python Elasticsearch client library
    client = Elasticsearch("http://localhost:9200")

    # attempt to index the dictionary entries using the helpers.bulk() method
    try:
        print("nAttempting to index the dictionary entries using helpers.bulk()")

        # use the helpers library's Bulk API to index list of Elasticsearch docs
        response = helpers.bulk(client, doc_list, index='labels_en_index', doc_type='_doc')

        # print the response returned by Elasticsearch
        print("helpers.bulk() RESPONSE:", json.dumps(response, indent=4))

    except Exception as err:

        # print any errors returned by the Elasticsearch cluster
        print("helpers.bulk() ERROR:", err)

    # print the time that has elapsed since the start of the script
    print("time elapsed:", time.time() - start_time)





