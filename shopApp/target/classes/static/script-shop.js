
   function showItems(items){
    const newDiv = document.getElementById("itemsDiv");
    const unorderedList = document.createElement('ul');

    unorderedList.setAttribute('id', 'theList');

    for(i = 0; i <= items.length - 1; i++){

        var li = document.createElement('li');
        li.innerHTML = items[i].id + '. ' +  items[i].name + ' price: ' + items[i].price;

        unorderedList.appendChild(li);

    }

    itemsDiv.appendChild(unorderedList);
}