import React from 'react';
import { UncontrolledCarousel } from 'reactstrap';

const items = [
    {
        src: 'https://zira.uz/wp-content/uploads/2020/09/non-palov.jpg',
        key: '1',
    },
    {
        src: 'https://zira.uz/wp-content/uploads/2020/08/kai--natma-shurpa-1.jpg',
        key: '2'
    },
    {
        src: 'https://zira.uz/wp-content/uploads/2018/06/uygurskiy-lagman-5.jpg',
        key: '3'
    }
];

const Slider = () => <UncontrolledCarousel items={items} />;

export default Slider;
