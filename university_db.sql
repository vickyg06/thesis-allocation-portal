-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 23, 2026 at 03:13 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `university_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `topic`
--

CREATE TABLE `topic` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('BACHELOR_THESIS','MASTER_THESIS','PROJECT') DEFAULT NULL,
  `supervisor_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `topic`
--

INSERT INTO `topic` (`id`, `title`, `type`, `supervisor_id`) VALUES
(1, 'AI Machine Learning', 'PROJECT', 1),
(2, 'Cryptography', 'PROJECT', 1),
(3, 'Advanced Robotics', 'BACHELOR_THESIS', 1),
(4, 'IoT', 'MASTER_THESIS', 1),
(5, 'Building a basic Discord Chatbot using Python.', 'PROJECT', 5),
(6, 'Machine Learning Algorithms for Image Recognition.', 'BACHELOR_THESIS', 5);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMINISTRATOR','ASSISTANT','STUDENT') NOT NULL,
  `student_limit` int(11) DEFAULT NULL,
  `bach_thesis_id` bigint(20) DEFAULT NULL,
  `master_thesis_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`DTYPE`, `id`, `email`, `name`, `password`, `role`, `student_limit`, `bach_thesis_id`, `master_thesis_id`, `project_id`) VALUES
('Assistant', 1, 'smith@uni.at', 'Dr. Smith', '1234', 'ASSISTANT', 4, NULL, NULL, NULL),
('Admin', 2, 'admin@uni.at', 'Super Boss', '1234', 'ADMINISTRATOR', NULL, NULL, NULL, NULL),
('Student', 3, 'max@uni.at', 'Max Mustermann', '1234', 'STUDENT', NULL, 3, NULL, 1),
('Student', 4, 'gabi@uni.at', 'Gabi Fischer', '1234', 'STUDENT', NULL, 6, 4, 2),
('Assistant', 5, 'mayer@uni.at', 'John Mayer', '1234', 'ASSISTANT', 3, NULL, NULL, NULL),
('Student', 6, 'lukas@uni.at', 'Lukas Weber', '1234', 'STUDENT', NULL, NULL, NULL, 5),
('Student', 7, 'mia@uni.at', 'Mia Miller', '1234', 'STUDENT', NULL, NULL, NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `topic`
--
ALTER TABLE `topic`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKsyxae888tt4byhobae7w1aufl` (`supervisor_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKbm3ga4a2pjod18o84jsdisx0g` (`bach_thesis_id`),
  ADD UNIQUE KEY `UK703apnbefbf2clj295p4aura7` (`master_thesis_id`),
  ADD UNIQUE KEY `UKscjj5839ys8jwy0hl5swn1fj6` (`project_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `topic`
--
ALTER TABLE `topic`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `topic`
--
ALTER TABLE `topic`
  ADD CONSTRAINT `FKsyxae888tt4byhobae7w1aufl` FOREIGN KEY (`supervisor_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FKi1r4ntqmuu3wpq2emadt10w6f` FOREIGN KEY (`project_id`) REFERENCES `topic` (`id`),
  ADD CONSTRAINT `FKibvapry649g172er7wtqhal6g` FOREIGN KEY (`bach_thesis_id`) REFERENCES `topic` (`id`),
  ADD CONSTRAINT `FKok3h6kno4jumkilpdycwfwk5h` FOREIGN KEY (`master_thesis_id`) REFERENCES `topic` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
